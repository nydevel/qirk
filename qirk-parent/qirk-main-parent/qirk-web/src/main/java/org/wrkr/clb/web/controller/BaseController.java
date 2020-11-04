package org.wrkr.clb.web.controller;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.http.SessionAttribute;

public abstract class BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

    protected static final String EXCEPTION_HANDLER_LOG_MESSAGE = "Exception caught at controller";

    protected final String className = getClass().getSimpleName();

    protected Long getLongParameter(FileItem item, String parameterName) throws BadRequestException {
        try {
            return Long.parseLong(item.getString());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Parameter '" + parameterName + "' must be of type Long.");
        }
    }

    protected User getSessionUser(HttpSession session) {
        return (User) session.getAttribute(SessionAttribute.AUTHN_USER);
    }

    protected void logProcessingTimeFromStartTime(long startTime, String method, Object... parameters) {
        if (!LOG.isInfoEnabled()) {
            return;
        }
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed response for method " + className + ":" + method + " " +
                "with parameters (" + StringUtils.join(Arrays.asList(parameters), ", ") + ") in " +
                resultTime + " ms");
    }
}
