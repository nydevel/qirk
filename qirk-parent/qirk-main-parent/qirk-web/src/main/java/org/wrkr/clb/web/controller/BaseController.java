/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
