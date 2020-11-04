package org.wrkr.clb.services.util.exception;

import javax.servlet.http.HttpServletResponse;

public class RequestEntityTooLargeException extends ApplicationException {

    private static final long serialVersionUID = -7930557596498066979L;

    public RequestEntityTooLargeException(String code, String message) {
        super(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, code, message);
    }
}
