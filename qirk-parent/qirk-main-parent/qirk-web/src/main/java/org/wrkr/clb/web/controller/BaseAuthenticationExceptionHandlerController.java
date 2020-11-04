package org.wrkr.clb.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
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
