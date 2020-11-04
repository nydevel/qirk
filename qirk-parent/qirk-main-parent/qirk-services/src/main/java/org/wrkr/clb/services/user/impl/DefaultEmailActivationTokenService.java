package org.wrkr.clb.services.user.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.crypto.HashEncoder;
import org.wrkr.clb.model.user.EmailActivationToken;
import org.wrkr.clb.repo.user.EmailActivationTokenRepo;
import org.wrkr.clb.services.user.EmailActivationTokenService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;


@Service
public class DefaultEmailActivationTokenService implements EmailActivationTokenService {

    private static final int TOKEN_LENGTH = 23;
    private static final int PASSWORD_LENGTH = 8;
    private static final long EMAIL_ACTIVATION_TOKEN_LIFETIME_MILLIS = 3 * 24 * 60 * 60 * 1000; // 3 days

    @Autowired
    private EmailActivationTokenRepo tokenRepo;

    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.NOT_SUPPORTED)
    public String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public EmailActivationToken getOrCreate(String email) {
        EmailActivationToken activationToken = tokenRepo.getByEmail(email);
        if (activationToken == null) {
            String token = generateToken();
            while (tokenRepo.existsByToken(token)) {
                token = generateToken();
            }

            activationToken = new EmailActivationToken();
            activationToken.setToken(token);
            activationToken.setEmailAddress(email);
        }

        String password = generatePassword();
        activationToken.setPassword(password);
        activationToken.setPasswordHash(HashEncoder.encryptToHex(password));
        activationToken.setExpiresAt(System.currentTimeMillis() + EMAIL_ACTIVATION_TOKEN_LIFETIME_MILLIS);

        if (activationToken.getId() == null) {
            tokenRepo.persist(activationToken);
        } else {
            activationToken = tokenRepo.merge(activationToken);
        }
        return activationToken;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public EmailActivationToken getAndDelete(String token) throws ApplicationException {
        EmailActivationToken activationToken = tokenRepo.getNotExpiredByToken(token);
        if (activationToken == null) {
            throw new NotFoundException("Token");
        }
        tokenRepo.deleteById(activationToken.getId());
        return activationToken;
    }
}
