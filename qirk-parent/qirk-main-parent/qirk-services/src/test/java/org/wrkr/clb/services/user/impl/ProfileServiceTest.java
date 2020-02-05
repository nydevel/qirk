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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.wrkr.clb.common.crypto.HashEncoder;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.PasswordActivationTokenRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.dto.user.PasswordChangeDTO;
import org.wrkr.clb.services.user.ProfileService;


@SuppressWarnings("unused")
public class ProfileServiceTest extends BaseServiceTest {

    private static Long enabledUserId;
    private static final String enabledUserEmail = "enableduser@test.com";
    private static final String enabledUserPassword = "enabled";
    private static final String enabledUserNewPassword = "enablednew";

    private static Long enabledUserWithTokenId;
    private static final String enabledUserWithTokenEmail = "enableduserwithtoken@test.com";
    private static final String enabledUserWithTokenPassword = "enabledwithtoken";
    private static final String enabledUserWithTokenToken = Long.valueOf(10 ^ 23).toString();

    private static final String nonExistingPassword = "nonexisting";

    private static long numberOfActivationTokens;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordActivationTokenRepo activationTokenRepo;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Long saveUserWithToken(String email, String password, String token)
            throws Exception {
        User user = saveUser(email, password);

        PasswordActivationToken activationToken = new PasswordActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setCreatedAt(user.getCreatedAt());
        testRepo.persistEntity(activationToken);

        return user.getId();
    }

    private EmailAddressDTO createEmailDTO(String email) {
        EmailAddressDTO dto = new EmailAddressDTO();
        dto.emailAddress = email;
        return dto;
    }

    private PasswordChangeDTO createPasswordChangeDTO(String password, String newPassword, String token) {
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.password = password;
        dto.newPassword = newPassword;
        dto.token = token;
        return dto;
    }

    @Before
    public void beforeTest() throws Exception {
        enabledUserId = saveUser(enabledUserEmail, enabledUserPassword).getId();
        enabledUserWithTokenId = saveUserWithToken(enabledUserWithTokenEmail, enabledUserWithTokenPassword,
                enabledUserWithTokenToken);

        numberOfActivationTokens = testRepo.countEntities(PasswordActivationToken.class);
    }

    @After
    public void afterTest() {
        testRepo.clearTable(PasswordActivationToken.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_resetPasswordForEnabledUser() throws Exception {
        EmailAddressDTO dto = createEmailDTO(enabledUserEmail);

        profileService.resetPassword(dto);

        long tokenCount = testRepo.countEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be created", numberOfActivationTokens + 1L, tokenCount);
        PasswordActivationToken token = activationTokenRepo.getByEmailAndFetchUser(enabledUserEmail);
        assertEquals("user id doesn't match", enabledUserId, token.getUser().getId());
    }

    /*@formatter:off
    @Test
    public void test_changePasswordForAuthenticatedEnabledUser() throws Exception {
        PasswordChangeDTO dto = createPasswordChangeDTO(enabledUserPassword, enabledUserNewPassword, null);

        profileService.changePassword(testRepo.getEntity(User.class, enabledUserId), dto);

        User user = userRepo.getByEmail(enabledUserEmail);
        assertEquals("password didn't change", HashEncoder.encryptToHex(enabledUserNewPassword), user.getPasswordHash());

        long tokenCount = testRepo.countEntities(PasswordActivationToken.class);
        assertEquals("no token should be deleted", numberOfActivationTokens, tokenCount);
    }

    @Test
    public void test_changePasswordForAuthenticatedEnabledUserWithWrongPassword() throws Exception {
        expectedException.expect(BadCredentialsException.class);

        PasswordChangeDTO dto = createPasswordChangeDTO(enabledUserWithTokenPassword, nonExistingPassword, null);

        profileService.changePassword(testRepo.getEntity(User.class, enabledUserId), dto);
    }

    @Test
    public void test_changePasswordByTokenForEnabledUserWithToken() throws Exception {
        PasswordChangeDTO dto = createPasswordChangeDTO(null, enabledUserNewPassword, enabledUserWithTokenToken);

        profileService.changePassword(null, dto);

        User user = userRepo.get(enabledUserWithTokenId);
        assertEquals("password didn't change", HashEncoder.encryptToHex(enabledUserNewPassword), user.getPasswordHash());

        long tokenCount = testRepo.countEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be deleted", numberOfActivationTokens - 1L, tokenCount);
        PasswordActivationToken token = activationTokenRepo.getByToken(enabledUserWithTokenToken);
        assertNull("token must be deleted", token);
    }

    @Test
    public void test_changePasswordByTokenForAuthenticatedEnabledUserWithToken() throws Exception {
        PasswordChangeDTO dto = createPasswordChangeDTO(null, enabledUserNewPassword, enabledUserWithTokenToken);

        profileService.changePassword(testRepo.getEntity(User.class, enabledUserWithTokenId), dto);

        User user = userRepo.get(enabledUserWithTokenId);
        assertEquals("password didn't change", HashEncoder.encryptToHex(enabledUserNewPassword), user.getPasswordHash());

        long tokenCount = testRepo.countEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be deleted", numberOfActivationTokens - 1L, tokenCount);
        PasswordActivationToken token = activationTokenRepo.getByToken(enabledUserWithTokenToken);
        assertNull("token must be deleted", token);
    }

    @Test
    public void test_changePasswordByTokenForEnabledUserWithTokenAuthenticatedAsOtherUser() throws Exception {
        PasswordChangeDTO dto = createPasswordChangeDTO(null, enabledUserNewPassword, enabledUserWithTokenToken);

        profileService.changePassword(testRepo.getEntity(User.class, enabledUserId), dto);

        User user = userRepo.get(enabledUserWithTokenId);
        assertEquals("password didn't change", HashEncoder.encryptToHex(enabledUserNewPassword), user.getPasswordHash());

        long tokenCount = testRepo.countEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be deleted", numberOfActivationTokens - 1L, tokenCount);
        PasswordActivationToken token = activationTokenRepo.getByToken(enabledUserWithTokenToken);
        assertNull("token must be deleted", token);
    }
    @formatter:on*/
}
