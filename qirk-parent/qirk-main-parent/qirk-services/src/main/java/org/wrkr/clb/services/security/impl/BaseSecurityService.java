package org.wrkr.clb.services.security.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.model.user.User;

public abstract class BaseSecurityService {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected boolean _isAuthenticated(User user) {
        return (user != null);
    }

    protected void requireAuthnOrThrowException(User user) throws SecurityException {
        if (!_isAuthenticated(user)) {
            throw new SecurityException("User is not authenticated");
        }
    }

    protected void requireManagerOrThrowException(User user) throws SecurityException {
        requireAuthnOrThrowException(user);
        if (!user.isManager()) {
            throw new SecurityException("User is not a manager");
        }
    }
}
