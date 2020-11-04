package org.wrkr.clb.services.user.impl;

import static org.junit.Assert.assertEquals;

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

public class PasswordActivationTokenServiceTest extends BaseServiceTest {

    private Long user1Id;
    private static final String user1Email = "user1@test.com";
    private static final String user1Password = "user1";

    private Long userWithTokenId;
    private static final String userWithTokenEmail = "userwithtoken@test.com";
    private static final String userWithTokenPassword = "withtoken";
    private static final String userWithTokenToken = Long.valueOf(10 ^ 23).toString();

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
        user1Id = saveUser(user1Email, user1Password).getId();
        userWithTokenId = saveUserWithToken(
                userWithTokenEmail, userWithTokenPassword, userWithTokenToken);
    }

    @After
    public void afterTest() {
        testRepo.clearTable(PasswordActivationToken.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_create() {
        User user = testRepo.getEntity(User.class, user1Id);
        long numberOfActivationTokens = testRepo.countEntities(PasswordActivationToken.class);

        tokenService.create(user);

        List<PasswordActivationToken> tokenList = testRepo.listEntities(PasswordActivationToken.class);
        assertEquals("exactly 1 token should be created", numberOfActivationTokens + 1, tokenList.size());
        boolean exists = tokenRepo.existsByUserId(user.getId());
        assertEquals("activation token doesn't exist", true, exists);
    }

    @Test
    public void test_getUserAndDeleteToken() throws Exception {
        long numberOfActivationTokens = testRepo.countEntities(PasswordActivationToken.class);

        User user = tokenService.getUserAndDeleteToken(userWithTokenToken);

        assertEquals("user id doesn't match", userWithTokenId, user.getId());
        boolean exists = tokenRepo.existsByToken(userWithTokenToken);
        assertEquals("token must be deleted", false, exists);
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
