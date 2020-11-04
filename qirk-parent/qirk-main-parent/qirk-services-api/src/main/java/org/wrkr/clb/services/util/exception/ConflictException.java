package org.wrkr.clb.services.util.exception;

import javax.servlet.http.HttpServletResponse;

import org.wrkr.clb.services.util.http.JsonStatusCode;

public class ConflictException extends ApplicationException {

    private static final long serialVersionUID = -7998228990025737759L;

    public ConflictException(String message) {
        super(HttpServletResponse.SC_CONFLICT, JsonStatusCode.CONFLICT, message);
    }
}
