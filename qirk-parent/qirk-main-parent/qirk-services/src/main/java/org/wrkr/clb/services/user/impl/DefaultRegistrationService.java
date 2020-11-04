package org.wrkr.clb.services.user.impl;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.common.mail.UserMailService;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.user.EmailActivationToken;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.NotificationSettingsRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.user.ActivationDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.user.EmailActivationTokenService;
import org.wrkr.clb.services.user.RegistrationService;

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
    private EmailActivationTokenService emailTokenService;

    @Autowired
    private ElasticsearchUserService elasticsearchService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ExistsDTO checkEmail(String email) {
        return new ExistsDTO(userRepo.existsByEmail(email));
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ExistsDTO checkUsername(String username) {
        return new ExistsDTO(userRepo.existsByUsername(username));
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public User createUserWithEmailAndPasswordHash(String email, String passwordHash, String username, String fullName)
            throws Exception {
        User user = new User();

        user.setEmailAddress(email);
        user.setPasswordHash(passwordHash);
        user.setUsername(username);
        user.setFullName(fullName);

        OffsetDateTime createdAt = DateTimeUtils.now();
        user.setCreatedAt(createdAt);

        userRepo.persist(user);

        NotificationSettings notifSettings = new NotificationSettings();
        notifSettings.setUserId(user.getId());
        notifSettingsRepo.save(notifSettings);

        try {
            elasticsearchService.index(user);
        } catch (Exception e) {
            LOG.error("Could not save user " + user.getId() + " to elasticsearch", e);
        }

        return user;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public EmailSentDTO register(EmailAddressDTO registerDTO) throws Exception {
        EmailActivationToken activationToken = emailTokenService.getOrCreate(registerDTO.emailAddress.toLowerCase());
        return mailService.sendRegistrationEmail(
                activationToken.getEmailAddress(), activationToken.getPassword(), activationToken.getToken());
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public User activate(ActivationDTO activationDTO) throws Exception {
        EmailActivationToken activationToken = emailTokenService.getAndDelete(activationDTO.token);
        User user = createUserWithEmailAndPasswordHash(
                activationToken.getEmailAddress(), activationToken.getPasswordHash(),
                activationDTO.username, activationDTO.fullName.strip());
        return user;
    }
}
