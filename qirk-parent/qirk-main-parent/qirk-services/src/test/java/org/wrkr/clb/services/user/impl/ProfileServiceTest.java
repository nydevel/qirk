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

    private Long user1Id;
    private static final String user1Email = "user1@test.com";
    private static final String user1Password = "user1";
    private static final String user1NewPassword = "new";

    private Long userWithTokenId;
    private static final String userWithTokenEmail = "userwithtoken@test.com";
    private static final String userWithTokenPassword = "withtoken";
    private static final String userWithTokenToken = Long.valueOf(10 ^ 23).toString(); // 24 symbols

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
        user1Id = saveUser(user1Email, user1Password).getId();
        userWithTokenId = saveUserWithToken(userWithTokenEmail, userWithTokenPassword,
                userWithTokenToken);

        numberOfActivationTokens = testRepo.countEntities(PasswordActivationToken.class);
    }

    @After
    public void afterTest() {
        testRepo.clearTable(PasswordActivationToken.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_resetPassword() throws Exception {
        EmailAddressDTO dto = createEmailDTO(user1Email);

        profileService.resetPassword(dto);

        long tokenCount = testRepo.countEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be created", numberOfActivationTokens + 1L, tokenCount);
        boolean exists = activationTokenRepo.existsByUserId(user1Id);
        assertEquals("activation token doesn't exist", true, exists);
    }
}
