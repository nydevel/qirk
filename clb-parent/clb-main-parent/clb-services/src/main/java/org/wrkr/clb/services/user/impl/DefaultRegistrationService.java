/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.services.user.impl;

import java.time.OffsetDateTime;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.HashEncoder;
import org.wrkr.clb.common.jms.statistics.StatisticsSender;
import org.wrkr.clb.common.jms.statistics.UserRegisteredMessage;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.common.mail.UserMailService;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.common.util.strings.ExtStringUtils;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.user.EmailActivationToken;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.NotificationSettingsRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.organization.OrganizationDTO;
import org.wrkr.clb.services.dto.user.ActivationDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.dto.user.RegisterDTO;
import org.wrkr.clb.services.dto.user.RegisterNoPasswordDTO;
import org.wrkr.clb.services.http.CookieService;
import org.wrkr.clb.services.organization.OrganizationService;
import org.wrkr.clb.services.user.EmailActivationTokenService;
import org.wrkr.clb.services.user.PasswordActivationTokenService;
import org.wrkr.clb.services.user.RegistrationService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.LicenseNotAcceptedException;
import org.wrkr.clb.services.util.http.Cookies;
import org.wrkr.clb.services.util.http.JsonStatusCode;


@Validated
@Service
public class DefaultRegistrationService implements RegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRegistrationService.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NotificationSettingsRepo notifSettingsRepo;

    @Autowired
    private UserMailService mailService;

    @Autowired
    private PasswordActivationTokenService tokenService;

    @Autowired
    private EmailActivationTokenService emailTokenService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private ElasticsearchUserService elasticsearchService;

    @Autowired
    private StatisticsSender statisticsSender;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ExistsDTO checkEmail(String email, boolean excludeDisabled) {
        return new ExistsDTO(userRepo.existsByEmail(email, excludeDisabled));
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ExistsDTO checkUsername(String username) {
        return new ExistsDTO(userRepo.existsByUsername(username));
    }

    private User createUserWithEmailAndPasswordHash(
            String email, String passwordHash, String username, String fullName, boolean enabled, boolean licenseAccepted)
            throws Exception {
        User user = new User();

        user.setEmailAddress(email);
        user.setPasswordHash(passwordHash);
        user.setUsername(username);
        user.setFullName(fullName);
        user.setLicenseAccepted(licenseAccepted);

        OffsetDateTime createdAt = DateTimeUtils.now();
        user.setCreatedAt(createdAt);

        userRepo.persist(user);

        NotificationSettings notifSettings = new NotificationSettings();
        notifSettings.setUserId(user.getId());
        notifSettingsRepo.save(notifSettings);

        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.name = user.getFullName();
        organizationDTO.uiId = "";
        Organization predefinedOrganization = organizationService.createPredefinedForUser(user, organizationDTO);
        user.setOrganizationMembership(predefinedOrganization.getMembers());

        if (enabled) {
            try {
                elasticsearchService.index(user);
                elasticsearchService.setOrganizations(user);
            } catch (Exception e) {
                LOG.error("Could not save user " + user.getId() + " to elasticsearch", e);
            }
        }

        return user;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public User createUserWithEmailAndPasswordHash(
            String email, String passwordHash, String username, String fullName, boolean licenseAccepted)
            throws Exception {
        return createUserWithEmailAndPasswordHash(email, passwordHash, username, fullName, true, licenseAccepted);
    }

    @Deprecated
    private User createDisabledUserWithEmailAndPassword(String email, String password) throws Exception {
        if (password == null || password.isBlank()) {
            throw new BadRequestException(JsonStatusCode.CONSTRAINT_VIOLATION, "Password must not be blank.");
        }
        email = email.toLowerCase();
        String username = ExtStringUtils.substringByLimitOrSymbols(email, 25, Arrays.asList("@"));
        return createUserWithEmailAndPasswordHash(
                email, HashEncoder.encryptToHex(password.strip()), username, username, false, false);
    }

    private void sendUserRegisteredStatistics(HttpServletRequest request, User user) {
        String cookieUuid = null;
        try {
            String userStatisticsCookie = cookieService.getCookieValue(request, Cookies.USER_STAT);
            if (userStatisticsCookie != null) {
                Integer lastColumnIndex = userStatisticsCookie.lastIndexOf(':');
                if (lastColumnIndex >= 0) {
                    cookieUuid = userStatisticsCookie.substring(0, lastColumnIndex);
                }
            }
        } catch (Exception e) {
            LOG.error("Could not get or parse cookie " + Cookies.USER_STAT);
        }
        if (cookieUuid != null) {
            statisticsSender.send(new UserRegisteredMessage(cookieUuid, user.getId(), user.getCreatedAt()));
        }
    }

    private PasswordActivationToken register(HttpServletRequest request, String email, String password) throws Exception {
        // create user and predefined organization
        User user = createDisabledUserWithEmailAndPassword(email, password);

        // create activation token
        PasswordActivationToken activationToken = tokenService.create(user);

        // send statistics
        sendUserRegisteredStatistics(request, user);

        return activationToken;
    }

    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public EmailSentDTO register(HttpServletRequest request, RegisterDTO registerDTO) throws Exception {
        PasswordActivationToken activationToken = register(request, registerDTO.emailAddress, registerDTO.password);

        // send confirmation email
        return mailService.sendConfirmationEmail(activationToken.getUser().getEmailAddress(), activationToken.getToken());
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public EmailSentDTO register(RegisterNoPasswordDTO registerDTO) throws Exception {
        if (!registerDTO.licenseAccepted) {
            throw new LicenseNotAcceptedException("");
        }
        EmailActivationToken activationToken = emailTokenService.getOrCreate(registerDTO.emailAddress.toLowerCase());
        return mailService.sendRegistrationEmail(
                activationToken.getEmailAddress(), activationToken.getPassword(), activationToken.getToken());
    }

    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public EmailSentDTO resendConfirmationEmail(EmailAddressDTO emailDTO) throws Exception {
        PasswordActivationToken activationToken = tokenService.getDisabledByEmail(emailDTO.emailAddress.strip());
        return mailService.sendConfirmationEmail(activationToken.getUser().getEmailAddress(), activationToken.getToken());
    }

    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public User activate(String token) throws Exception {
        User user = tokenService.getUserAndDeleteToken(token);

        boolean wasEnabled = user.isEnabled();
        user.setEnabled(true);
        user = userRepo.merge(user);

        if (!wasEnabled) {
            try {
                elasticsearchService.index(user);
                elasticsearchService.setOrganizations(user);
            } catch (Exception e) {
                LOG.error("Could not save user " + user.getId() + " to elasticsearch", e);
            }
        }

        return user;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public User activate(HttpServletRequest request, ActivationDTO activationDTO) throws Exception {
        EmailActivationToken activationToken = emailTokenService.getAndDelete(activationDTO.token);
        User user = createUserWithEmailAndPasswordHash(
                activationToken.getEmailAddress(), activationToken.getPasswordHash(),
                activationDTO.username, activationDTO.fullName.strip(), true);
        sendUserRegisteredStatistics(request, user);
        return user;
    }
}
