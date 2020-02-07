/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
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

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.HashEncoder;
import org.wrkr.clb.common.crypto.TokenGenerator;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.crypto.token.notification.NotificationTokenData;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.common.mail.UserMailService;
import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.LanguageRepo;
import org.wrkr.clb.repo.TagRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.repo.user.NotificationSettingsRepo;
import org.wrkr.clb.repo.user.PasswordActivationTokenRepo;
import org.wrkr.clb.services.TagService;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.user.CurrentUserProfileDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.dto.user.LoginDTO;
import org.wrkr.clb.services.dto.user.PasswordChangeDTO;
import org.wrkr.clb.services.dto.user.PriofileUpdateDTO;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.user.PasswordActivationTokenService;
import org.wrkr.clb.services.user.ProfileService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;
import org.wrkr.clb.services.util.http.SessionAttribute;

//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultProfileService implements ProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultProfileService.class);

    // chat token config values
    private Integer notificationTokenNotBeforeToleranceSeconds;
    private Integer notificationTokenLifetimeSeconds;

    public void setNotificationTokenNotBeforeToleranceSeconds(Integer notificationTokenNotBeforeToleranceSeconds) {
        this.notificationTokenNotBeforeToleranceSeconds = notificationTokenNotBeforeToleranceSeconds;
    }

    public void setNotificationTokenLifetimeSeconds(Integer notificationTokenLifetimeSeconds) {
        this.notificationTokenLifetimeSeconds = notificationTokenLifetimeSeconds;
    }

    @Autowired
    private JDBCUserRepo userRepo;

    @Autowired
    private PasswordActivationTokenRepo activationTokenRepo;

    @Autowired
    private NotificationSettingsRepo notifSettingsRepo;

    @Autowired
    private TagService tagService;

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private PasswordActivationTokenService activationTokenService;

    @Autowired
    private UserMailService mailService;

    @Autowired
    private ElasticsearchUserService elasticsearchService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public EmailSentDTO resetPassword(EmailAddressDTO emailDTO) throws ApplicationException {
        User user = userRepo.getByEmail(emailDTO.emailAddress);
        if (user == null) {
            throw new NotFoundException("User");
        }

        PasswordActivationToken activationToken = activationTokenRepo.getByUser(user);
        if (activationToken == null) {
            activationToken = activationTokenService.create(user);
        }
        return mailService.sendPasswordResetEmail(user.getEmailAddress(), activationToken.getToken());
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void changePassword(User sessionUser, PasswordChangeDTO passwordDTO) throws Exception {
        User user = null;
        if (passwordDTO.token != null) {
            user = activationTokenService.getUserAndDeleteToken(passwordDTO.token);
            // jpaUserRepo.detach(user);
        } else {
            // security start
            securityService.isAuthenticated(sessionUser);
            // security finish

            user = userRepo.getByIdForAccount(sessionUser.getId());
            if (user == null || passwordDTO.password == null
                    || !user.getPasswordHash().equals(HashEncoder.encryptToHex(passwordDTO.password))) {
                throw new BadCredentialsException("");
            }
        }

        user.setPasswordHash(HashEncoder.encryptToHex(passwordDTO.newPassword));
        userRepo.updatePasswordHash(user);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public User getAccount(LoginDTO loginDTO) {
        if (loginDTO.username.indexOf('@') >= 0) {
            return userRepo.getByEmailForAccount(loginDTO.username);
        }
        return userRepo.getByUsernameForAccount(loginDTO.username);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public CurrentUserProfileDTO getProfile(User sessionUser) throws ApplicationException {
        // security start
        securityService.isAuthenticated(sessionUser);
        // security finish

        User user = userRepo.getByIdAndFetchNotificationSettings(sessionUser.getId());
        if (user == null) {
            throw new NotFoundException("User");
        }

        user.setTags(tagRepo.listByUserId(user.getId()));
        user.setLanguages(languageRepo.listByUserId(user.getId()));
        // user.setLinks(profileLinkRepo.getByUser(user));
        return CurrentUserProfileDTO.fromEntity(user);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public CurrentUserProfileDTO updateProfile(HttpSession session, User sessionUser, PriofileUpdateDTO profileDTO)
            throws ApplicationException {
        // security start
        securityService.isAuthenticated(sessionUser);
        // security finish

        User user = userRepo.getById(sessionUser.getId());
        if (user == null) {
            throw new NotFoundException("User");
        }

        user.setFullName(profileDTO.fullName.strip());
        userRepo.updateFullName(user);

        Long userId = user.getId();
        NotificationSettings notifSettings = profileDTO.notificationSettings;
        notifSettings.setUserId(userId);
        notifSettingsRepo.update(notifSettings);
        user.setNotificationSettings(notifSettings);

        List<Tag> tags = tagService.setTagsToUser(user, profileDTO.tagNames);

        List<Language> languages = languageRepo.listByIds(profileDTO.languageIds);
        languageRepo.setLanguagesToUser(user.getId(), languages);

        session.setAttribute(SessionAttribute.AUTHN_USER, user);
        try {
            elasticsearchService.updateOrIndex(user);
        } catch (Exception e) {
            LOG.error("Could not update user " + user.getId() + " in elasticsearch", e);
        }
        return CurrentUserProfileDTO.fromEntity(user, tags, languages);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public TokenAndIvDTO getNotificationToken(User sessionUser) throws Exception {
        // security star
        securityService.isAuthenticated(sessionUser);
        // security finish

        NotificationTokenData tokenData = new NotificationTokenData();
        tokenData.subscriberId = sessionUser.getId();

        long now = System.currentTimeMillis();
        tokenData.notBefore = now - notificationTokenNotBeforeToleranceSeconds * 1000;
        tokenData.notOnOrAfter = now + notificationTokenLifetimeSeconds * 1000;

        return tokenGenerator.encrypt(tokenData.toJson());
    }
}
