package org.wrkr.clb.services.util.exception;

import org.springframework.security.core.AuthenticationException;

public class TooManyLoginAttemptsException extends AuthenticationException {

    private static final long serialVersionUID = -4323912789490290302L;

    public static final int SC_TOO_MANY_REQUESTS = 429;

    private long retryAfterMillis;

    public TooManyLoginAttemptsException(long retryAfterMillis, String message) {
        super(message);
        this.retryAfterMillis = retryAfterMillis;
    }

    public long getRetryAfterMillis() {
        return retryAfterMillis;
    }
}
