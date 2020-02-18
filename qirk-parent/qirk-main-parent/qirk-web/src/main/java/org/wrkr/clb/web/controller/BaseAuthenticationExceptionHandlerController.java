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
package org.wrkr.clb.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.wrkr.clb.services.dto.RetryAfterDTO;
import org.wrkr.clb.services.util.exception.TooManyLoginAttemptsException;
import org.wrkr.clb.services.util.http.JsonStatusCode;
import org.wrkr.clb.web.json.JsonContainer;

public abstract class BaseAuthenticationExceptionHandlerController extends BaseExceptionHandlerController {

    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private JsonContainer<Void, Void> handleInvalidCredentials(@SuppressWarnings("unused") AuthenticationException e) {
        return JsonContainer.fromCodeAndReason(JsonStatusCode.INVALID_CREDENTIALS,
                "No account found with the given credentials.");
    }

    @ExceptionHandler(TooManyLoginAttemptsException.class)
    private JsonContainer<Void, RetryAfterDTO> handleTooManyLoginAttemptsException(HttpServletResponse response,
            TooManyLoginAttemptsException e) {
        response.setStatus(TooManyLoginAttemptsException.SC_TOO_MANY_REQUESTS);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.TOO_MANY_LOGIN_ATTEMPTS, "Too many login attempts.",
                new RetryAfterDTO(e.getRetryAfterMillis() / 1000L));
    }
}
