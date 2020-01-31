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
        activationTokenRepo.persist(activationToken);
        return activationToken;
    }

    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.MANDATORY)
    public PasswordActivationToken getDisabledByEmail(String email) throws ApplicationException {
        PasswordActivationToken token = activationTokenRepo.getDisabledByEmailAndFetchUser(email);
        if (token == null) {
            throw new NotFoundException("Token");
        }
        return token;
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
