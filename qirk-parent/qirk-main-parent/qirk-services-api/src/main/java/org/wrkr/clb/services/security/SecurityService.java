package org.wrkr.clb.services.security;

import javax.validation.constraints.NotNull;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;

@Validated
public interface SecurityService {

    public void isAuthenticated(User user) throws AuthenticationCredentialsNotFoundException;

    public void authzCanReadUserProfile(User currentUser,
            @NotNull(message = "userId must not be null") Long userId) throws SecurityException;
}
