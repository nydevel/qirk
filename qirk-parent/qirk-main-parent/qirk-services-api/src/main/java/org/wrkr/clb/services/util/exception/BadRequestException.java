package org.wrkr.clb.services.util.exception;

import javax.servlet.http.HttpServletResponse;

import org.wrkr.clb.services.util.http.JsonStatusCode;

public class BadRequestException extends ApplicationException {

    private static final long serialVersionUID = -7370720392930075338L;

    public BadRequestException(String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, JsonStatusCode.BAD_REQUEST, message);
    }

    public BadRequestException(String code, String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, code, message);
    }

    public BadRequestException(String code, String message, Throwable cause) {
        super(HttpServletResponse.SC_BAD_REQUEST, code, message, cause);
    }
}
