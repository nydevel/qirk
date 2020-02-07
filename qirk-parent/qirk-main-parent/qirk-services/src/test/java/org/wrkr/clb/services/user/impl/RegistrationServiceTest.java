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

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.PasswordActivationTokenRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.user.RegisterDTO;
import org.wrkr.clb.services.user.RegistrationService;

@SuppressWarnings("unused")
public class RegistrationServiceTest extends BaseServiceTest {

    private static final String user1email = "user1@test.com";
    private static final String user1password = "111111";

    private static final String user2email = "user2@test.com";
    private static final String user2password = "222222";

    private static final String nonExistingToken = Long.valueOf(-10 ^ 22).toString(); // minus never appears in tokens

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordActivationTokenRepo activationTokenRepo;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @After
    public void afterTest() {
        testRepo.clearTable(PasswordActivationToken.class);
        testRepo.clearTable(User.class);
    }

    private static RegisterDTO createRegisterDTO(String email, String password) {
        RegisterDTO dto = new RegisterDTO();
        dto.emailAddress = email;
        dto.password = password;
        return dto;
    }

    /*@formatter:off
    @Test
    public void test_register() throws Exception {
        RegisterDTO dto = createRegisterDTO(user1email, user1password);

        registrationService.register(null, dto);

        List<User> userList = testRepo.listEntities(User.class);
        assertEquals("exactly 1 user should be created", userList.size(), 1);
        User registeredUser = userList.get(0);
        assertEquals("email doesn't match", dto.email, registeredUser.getEmailAddress());
        assertEquals("username doesn't match", dto.email, registeredUser.getUsername());
        assertEquals("password hash doesn't match", HashEncoder.encryptToHex(dto.password), registeredUser.getPasswordHash());
        assertEquals("user must not be enabled", false, registeredUser.isEnabled());
        assertEquals("full name doesn't match", dto.email.split("@")[0], registeredUser.getFullName());

        List<ActivationToken> tokenList = testRepo.listEntities(ActivationToken.class);
        assertEquals("exactly 1 token should be created", 1, tokenList.size());
        ActivationToken token = tokenList.get(0);
        assertEquals("user id doesn't match", registeredUser.getId(), token.getUser().getId());
        // assertEquals("creation timestamp doesn't match",
        // registeredUser.getCreatedAt(), token.getCreatedAt());
    }

    @Test
    public void test_registerWithoutEmail() throws Exception {
        expectedException.expect(ConstraintViolationException.class);

        RegisterDTO dto = createRegisterDTO(null, user1password);
        registrationService.register(null, dto);
    }

    @Test
    public void test_registerEmptyEmail() throws Exception {
        expectedException.expect(ConstraintViolationException.class);

        RegisterDTO dto = createRegisterDTO("", user1password);
        registrationService.register(null, dto);
    }

    @Test
    public void test_registerSameEmail() throws Exception {
        expectedException.expect(PersistenceException.class);
        expectedException.expectCause(IsInstanceOf.instanceOf(org.hibernate.exception.ConstraintViolationException.class));

        RegisterDTO dto = createRegisterDTO(user1email, user1password);
        registrationService.register(null, dto);

        RegisterDTO sameEmailDto = createRegisterDTO(user1email, user2password);
        registrationService.register(null, sameEmailDto);
    }

    @Test
    public void test_registerWithoutPassword() throws Exception {
        expectedException.expect(ConstraintViolationException.class);

        RegisterDTO dto = createRegisterDTO(user1email, null);
        registrationService.register(null, dto);
    }

    @Test
    public void test_registerWithEmptyPassword() throws Exception {
        expectedException.expect(ConstraintViolationException.class);

        RegisterDTO dto = createRegisterDTO(user1email, "");
        registrationService.register(null, dto);
    }
    @formatter:on*/

    @Test
    public void test_checkNonExistingEmail() throws Exception {
        ExistsDTO dto = registrationService.checkEmail(user1email, false);
        assertEquals("email must not exist", false, dto.exists);
    }

    /*@formatter:off
    @Test
    public void test_checkNonExistingEmailAfterRegistration() throws Exception {
        RegisterDTO registerDTO = createRegisterDTO(user1email, user1password);
        registrationService.register(null, registerDTO);

        ExistsDTO existsDTO = registrationService.checkEmail(user2email, false);
        assertEquals("email must exist", false, existsDTO.exists);
    }

    @Test
    public void test_checkExistingEmail() throws Exception {
        RegisterDTO registerDTO = createRegisterDTO(user1email, user1password);
        registrationService.register(null, registerDTO);

        ExistsDTO existsDTO = registrationService.checkEmail(user1email, false);
        assertEquals("email must exist", true, existsDTO.exists);
    }
    
    @Test
    public void test_activate() throws Exception {
        RegisterDTO dto = createRegisterDTO(user1email, user1password);
        registrationService.register(null, dto);
        User registeredUser = userRepo.getByEmail(dto.email);

        registrationService.activate(activationTokenRepo.getByUser(registeredUser).getToken());

        User activatedUser = userRepo.getByEmail(dto.email);
        assertEquals("id doesn't match", registeredUser.getId(), activatedUser.getId());
        assertEquals("user must be enabled", activatedUser.isEnabled(), true);
        assertNull("token must be deleted", activationTokenRepo.getByUser(activatedUser));

        List<ActivationToken> tokenList = testRepo.listEntities(ActivationToken.class);
        assertEquals("token must be deleted", 0, tokenList.size());
    }

    @Test
    public void test_activateByMissingToken() throws Exception {
        expectedException.expect(ApplicationException.class);
        expectedException.expect(JsonStatusCodeMatcher.hasCode(JsonStatusCode.NOT_FOUND));

        registrationService.activate(nonExistingToken);
    }

    @Test
    public void test_activateTwice() throws Exception {
        expectedException.expect(ApplicationException.class);
        expectedException.expect(JsonStatusCodeMatcher.hasCode(JsonStatusCode.NOT_FOUND));

        RegisterDTO dto = createRegisterDTO(user1email, user1password);
        registrationService.register(null, dto);
        User registeredUser = userRepo.getByEmail(dto.email);
        ActivationToken token = activationTokenRepo.getByUser(registeredUser);
        registrationService.activate(token.getToken());

        registrationService.activate(token.getToken());
    }
    @formatter:on*/
}
