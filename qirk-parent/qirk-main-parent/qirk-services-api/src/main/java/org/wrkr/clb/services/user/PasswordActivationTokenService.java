package org.wrkr.clb.services.user;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;

@Validated
public interface PasswordActivationTokenService {

    public PasswordActivationToken create(
            @NotNull(message = "user must not be null") User user);

    public User getUserAndDeleteToken(
            @NotNull(message = "token must not be null") String token)
            throws Exception;
}
