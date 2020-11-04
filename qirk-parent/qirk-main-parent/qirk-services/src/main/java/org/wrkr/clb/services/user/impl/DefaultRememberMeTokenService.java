package org.wrkr.clb.services.user.impl;

import java.time.OffsetDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.auth.RememberMeToken;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.auth.RememberMeTokenRepo;
import org.wrkr.clb.services.user.RememberMeTokenService;


@Service
public class DefaultRememberMeTokenService implements RememberMeTokenService {

    private static final int TOKEN_LENGTH = 72;

    @Autowired
    private RememberMeTokenRepo rememberMeTokenRepo;

    private RememberMeToken generateToken() {
        RememberMeToken token = new RememberMeToken();
        token.setToken(RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH));
        return token;
    }

    @Override
    @Transactional(value = "authTransactionManager", rollbackFor = Throwable.class)
    public RememberMeToken rememberMe(User user) {
        RememberMeToken token = generateToken();
        token.setUserId(user.getId());

        OffsetDateTime now = DateTimeUtils.now();
        token.setCreatedAt(now);
        token.setUpdatedAt(now);

        rememberMeTokenRepo.save(token);
        return token;
    }

    @Override
    @Transactional(value = "authTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public Long getUserIdByRememberMeToken(String token) {
        if (token == null) {
            return null;
        }
        return rememberMeTokenRepo.getUserIdByToken(token);
    }

    @Override
    @Transactional(value = "authTransactionManager", rollbackFor = Throwable.class)
    public OffsetDateTime updateRememberMeToken(String token) {
        OffsetDateTime updatedAt = DateTimeUtils.now();
        rememberMeTokenRepo.updateUpdatedAtByToken(updatedAt, token);
        return updatedAt;
    }
}
