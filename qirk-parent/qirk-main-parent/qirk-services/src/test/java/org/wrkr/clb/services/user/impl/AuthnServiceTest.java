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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.user.LoginStatistics;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.dto.user.LoginDTO;
import org.wrkr.clb.services.user.AuthnService;


@SuppressWarnings("unused")
public class AuthnServiceTest extends BaseServiceTest {

    private static Long enabledUserId;
    private static final String enabledUserEmail = "enableduser@test.com";
    private static final String enabledUserPassword = "enabled";

    private static final String disabledUserEmail = "disableduser@test.com";
    private static final String disabledUserPassword = "disabled";

    private static final String nonExistingPassword = "nonexisting";

    private static final String forwarderForHeader = "127.0.0.1";

    @Autowired
    private AuthnService authnService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private LoginDTO createLoginDTO(String username, String password) {
        return new LoginDTO(username, password);
    }

    @Before
    public void beforeTest() throws Exception {
        enabledUserId = saveUser(enabledUserEmail, enabledUserPassword).getId();
        saveUser(disabledUserEmail, disabledUserPassword).getId();
    }

    @After
    public void afterTest() {
        testRepo.clearTable(LoginStatistics.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_() {

    }

    /*@formatter:off
    @Test
    public void test_loginWithoutUsername() throws Exception {
        expectedException.expect(ConstraintViolationException.class);

        authnService.login(createLoginDTO(null, enabledUserPassword), forwarderForHeader);
    }

    @Test
    public void test_loginWithEmptyUsername() throws Exception {
        expectedException.expect(BadCredentialsException.class);

        authnService.login(createLoginDTO("", enabledUserPassword), forwarderForHeader);
    }

    @Test
    public void test_loginEnabledUser() throws Exception {
        User user = authnService.login(createLoginDTO(enabledUserUsername, enabledUserPassword), forwarderForHeader);

        assertEquals("id doesn't match", user.getId(), enabledUserId);

        List<LoginStatistics> loginList = testRepo.listEntities(LoginStatistics.class);
        assertEquals("exactly 1 login statistics should be created", 1, loginList.size());
        LoginStatistics login = loginList.get(0);
        assertEquals("user id doesn't match", user.getId(), login.getUser().getId());
        assertEquals("IP doesn't match", forwarderForHeader, login.getInternetAddress());

        testRepo.clearTable(LoginStatistics.class);
    }

    @Test
    public void test_loginEnabledUserWithoutPassword() throws Exception {
        expectedException.expect(ConstraintViolationException.class);

        authnService.login(createLoginDTO(enabledUserUsername, null), forwarderForHeader);
    }

    @Test
    public void test_loginEnabledUserWithEmptyPassword() throws Exception {
        expectedException.expect(BadCredentialsException.class);

        authnService.login(createLoginDTO(enabledUserUsername, ""), forwarderForHeader);
    }

    @Test
    public void test_loginEnabledUserWithWrongPassword() throws Exception {
        expectedException.expect(BadCredentialsException.class);

        authnService.login(createLoginDTO(enabledUserUsername, nonExistingPassword), forwarderForHeader);
    }

    @Test
    public void test_loginDisabledUser() throws Exception {
        expectedException.expect(DisabledException.class);

        authnService.login(createLoginDTO(disabledUserUsername, disabledUserPassword), forwarderForHeader);
    }
    @formatter:on*/
}
