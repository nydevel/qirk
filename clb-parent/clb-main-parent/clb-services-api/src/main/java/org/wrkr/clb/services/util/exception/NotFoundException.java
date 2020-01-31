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
