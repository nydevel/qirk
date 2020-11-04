package org.wrkr.clb.services.user.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.PasswordActivationTokenRepo;
import org.wrkr.clb.services.user.PasswordActivationTokenService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;

@Validated
@Service
public class DefaultPasswordActivationTokenService implements PasswordActivationTokenService {

    private static final int TOKEN_LENGTH = 23;

    @Autowired
    private PasswordActivationTokenRepo activationTokenRepo;

    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public PasswordActivationToken create(User user) {
        String token = generateToken();
        while (activationTokenRepo.existsByToken(token)) {
            token = generateToken();
        }

        PasswordActivationToken activationToken = new PasswordActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);

        activationToken.setCreatedAt(DateTimeUtils.now());
        activationTokenRepo.save(activationToken);
        return activationToken;
    }

    private User getUserAndDeleteToken(PasswordActivationToken activationToken) throws ApplicationException {
        if (activationToken == null) {
            throw new NotFoundException("Token");
        }
        User user = activationToken.getUser();
        activationTokenRepo.delete(activationToken);
        return user;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public User getUserAndDeleteToken(String token) throws ApplicationException {
        PasswordActivationToken activationToken = activationTokenRepo.getByTokenAndFetchUser(token);
        return getUserAndDeleteToken(activationToken);
    }
}
