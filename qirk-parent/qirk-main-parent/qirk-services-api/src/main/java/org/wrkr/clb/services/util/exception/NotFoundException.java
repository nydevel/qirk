package org.wrkr.clb.services.util.exception;

import javax.servlet.http.HttpServletResponse;

import org.wrkr.clb.services.util.http.JsonStatusCode;

public class NotFoundException extends ApplicationException {

    private static final long serialVersionUID = -6105735510926330248L;

    private static String generateExceptionMessage(String entityName) {
        return entityName + " not found.";
    }

    public NotFoundException(String entityName) {
        super(HttpServletResponse.SC_NOT_FOUND, JsonStatusCode.NOT_FOUND, generateExceptionMessage(entityName));
    }
}
