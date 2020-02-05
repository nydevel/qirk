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
