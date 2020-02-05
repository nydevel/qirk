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
package org.wrkr.clb.services.project.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.HashEncoder;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.ProjectInviteToken;
import org.wrkr.clb.repo.project.ProjectInviteTokenRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.dto.user.RegisteredDTO;
import org.wrkr.clb.services.project.ProjectInviteTokenService;
import org.wrkr.clb.services.user.EmailActivationTokenService;


@Validated
@Service
public class DefaultProjectInviteTokenService implements ProjectInviteTokenService {

    private static final int TOKEN_LENGTH = 23;

    @Autowired
    private ProjectInviteTokenRepo projectInviteTokenRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailActivationTokenService activationTokenService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public RegisteredDTO checkToken(String token) {
        String email = projectInviteTokenRepo.getEmailByToken(token);
        if (email == null) {
            return new RegisteredDTO(false);
        }
        return new RegisteredDTO(userRepo.existsByEmail(email));
    }

    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public ProjectInviteToken create(GrantedPermissionsProjectInvite invite, String email) {
        String token = generateToken();
        while (projectInviteTokenRepo.getTokenIfExists(token) != null) {
            token = generateToken();
        }

        ProjectInviteToken inviteToken = new ProjectInviteToken();

        String password = activationTokenService.generatePassword();
        inviteToken.setPassword(password);
        inviteToken.setPasswordHash(HashEncoder.encryptToHex(password));

        inviteToken.setInvite(invite);
        inviteToken.setToken(token);
        inviteToken.setEmailAddress(email);
        inviteToken.setCreatedAt(invite.getCreatedAt());

        projectInviteTokenRepo.persist(inviteToken);
        return inviteToken;
    }
}
