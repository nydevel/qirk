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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.PasswordActivationTokenRepo;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.user.PasswordActivationTokenService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.http.JsonStatusCode;
import org.wrkr.clb.test.util.JsonStatusCodeMatcher;

public class ActivationTokenServiceTest extends BaseServiceTest {

    private static Long enabledUserId;
    private static final String enabledUserEmail = "enableduser@test.com";
    private static final String enabledUserPassword = "enabled";

    private static final String disabledUserEmail = "disableduser@test.com";
    private static final String disabledUserPassword = "disabled";

    private static Long enabledUserWithTokenId;
    private static final String enabledUserWithTokenEmail = "enableduserwithtoken@test.com";
    private static final String enabledUserWithTokenPassword = "enabledwithtoken";
    private static final String enabledUserWithTokenToken = Long.valueOf(10 ^ 23).toString();

    private static final String nonExistingToken = Long.valueOf(-10 ^ 22).toString(); // minus never appears in tokens

    @Autowired
    private PasswordActivationTokenService tokenService;

    @Autowired
    private PasswordActivationTokenRepo tokenRepo;

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

    @Before
    public void beforeTest() throws Exception {
        enabledUserId = saveUser(enabledUserEmail, enabledUserPassword).getId();
        saveUser(disabledUserEmail, disabledUserPassword).getId();
        enabledUserWithTokenId = saveUserWithToken(enabledUserWithTokenEmail, enabledUserWithTokenPassword,
                enabledUserWithTokenToken);
    }

    @After
    public void afterTest() {
        testRepo.clearTable(PasswordActivationToken.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_createForEnabledUser() {
        User enabledUser = testRepo.getEntity(User.class, enabledUserId);
        long numberOfActivationTokens = testRepo.countEntities(PasswordActivationToken.class);

        PasswordActivationToken token = tokenService.create(enabledUser);

        List<PasswordActivationToken> tokenList = testRepo.listEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be created", numberOfActivationTokens + 1, tokenList.size());
        token = testRepo.getEntity(PasswordActivationToken.class, token.getId());
        assertEquals("user id doesn't match", enabledUser.getId(), token.getUser().getId());
    }

    @Test
    public void test_createForDisabledUser() {
        User disabledUser = testRepo.getEntity(User.class, enabledUserId);
        long numberOfActivationTokens = testRepo.countEntities(PasswordActivationToken.class);

        PasswordActivationToken token = tokenService.create(disabledUser);

        List<PasswordActivationToken> tokenList = testRepo.listEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be created", numberOfActivationTokens + 1, tokenList.size());
        token = testRepo.getEntity(PasswordActivationToken.class, token.getId());
        assertEquals("user id doesn't match", disabledUser.getId(), token.getUser().getId());
    }

    @Test
    public void test_getUserAndDeleteTokenForEnabledUser() throws Exception {
        long numberOfActivationTokens = testRepo.countEntities(PasswordActivationToken.class);

        User user = tokenService.getUserAndDeleteToken(enabledUserWithTokenToken);

        assertEquals("user id doesn't match", enabledUserWithTokenId, user.getId());
        PasswordActivationToken deletedToken = tokenRepo.getByToken(enabledUserWithTokenToken);
        assertNull("token must be deleted", deletedToken);
        List<PasswordActivationToken> tokenList = testRepo.listEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be deleted", numberOfActivationTokens - 1, tokenList.size());
    }

    @Test
    public void test_getUserAndDeleteNonExistingToken() throws Exception {
        expectedException.expect(ApplicationException.class);
        expectedException.expect(JsonStatusCodeMatcher.hasCode(JsonStatusCode.NOT_FOUND));

        tokenService.getUserAndDeleteToken(nonExistingToken);
    }
}
