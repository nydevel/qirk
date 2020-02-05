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
package org.wrkr.clb.services.user;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;


@Validated
public interface PasswordActivationTokenService {

    public PasswordActivationToken create(
            @NotNull(message = "user in ActivationTokenService must not be null") User user);

    @Deprecated
    public PasswordActivationToken getDisabledByEmail(
            @NotNull(message = "email in ActivationTokenService must not be null") String email) throws Exception;

    public User getUserAndDeleteToken(
            @NotNull(message = "token in ActivationTokenService must not be null") String token)
            throws Exception;
}
