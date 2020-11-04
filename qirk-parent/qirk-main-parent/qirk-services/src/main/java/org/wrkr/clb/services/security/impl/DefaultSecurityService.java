package org.wrkr.clb.services.security.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.services.security.SecurityService;

@Validated
@Service
public class DefaultSecurityService extends BaseSecurityService implements SecurityService {

    @SuppressWarnings("unused")
    @Autowired
    private JDBCUserRepo userRepo;

    @Override
    public void isAuthenticated(User user) throws AuthenticationCredentialsNotFoundException {
        if (!_isAuthenticated(user)) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated or disabled");
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void authzCanReadUserProfile(User currentUser, Long userId) throws SecurityException {
        /*@formatter:off
        User user = userRepo.getEnabledByIdForSecurity(userId);
        if (user == null || !user.isDontRecommend()) {
            return; // public can be read by everyone
        }
        if (!user.getId().equals(currentUser.getId())) {
            throw new SecurityException("User can't read another user's profile");
        }
        @formatter:on*/
    }
}
