package org.wrkr.clb.services.util.exception;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.services.util.http.JsonStatusCode;

public class ApplicationException extends Exception {

    private static final long serialVersionUID = 1871506672523044774L;

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationException.class);

    private int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    private String jsonStatusCode = JsonStatusCode.INTERNAL_SERVER_ERROR;

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String code, String message, Throwable cause) {
        super(message, cause);
        this.jsonStatusCode = code;
    }

    protected ApplicationException(int httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.jsonStatusCode = code;
    }

    protected ApplicationException(int httpStatus, String code, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.jsonStatusCode = code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getJsonStatusCode() {
        return jsonStatusCode;
    }
}
