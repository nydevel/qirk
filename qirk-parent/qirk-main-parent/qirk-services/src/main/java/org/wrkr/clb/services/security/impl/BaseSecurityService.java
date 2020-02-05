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
}
