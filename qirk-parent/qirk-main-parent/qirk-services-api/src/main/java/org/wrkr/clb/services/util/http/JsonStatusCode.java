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
package org.wrkr.clb.services.util.http;

public abstract class JsonStatusCode {

    // 200
    public static final String OK = "OK";

    // 400
    public static final String BAD_REQUEST = "INVALID";
    public static final String CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";
    public static final String INVALID_EMAIL = "INVALID_EMAIL";
    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";
    public static final String DEPRECATED = "DEPRECATED";
    public static final String INVALID_RECAPTCHA = "INVALID_RECAPTCHA";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";

    // 401
    public static final String UNAUTHORIZED = "NOT_AUTHENTICATED";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String ACCOUNT_DISABLED = "ACCOUNT_DISABLED";

    // 402
    public static final String PAYMENT_REQUIRED = "PAYMENT_REQUIRED";

    // 403
    public static final String INVALID_CSRF_TOKEN = "INVALID_CSRF_TOKEN";
    public static final String LICENSE_NOT_ACCEPTED = "LICENSE_NOT_ACCEPTED";
    public static final String FORBIDDEN = "PERMISSION_DENIED";

    // 404
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String HANDLER_NOT_FOUND = "HANDLER_NOT_FOUND";

    // 405
    public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";

    // 406
    public static final String NOT_ACCEPTABLE = "NOT_ACCEPTABLE";

    // 409
    public static final String CONFLICT = "CONFLICT";

    // 410
    public static final String GONE = "GONE";

    // 415
    public static final String UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE";

    // 429
    public static final String TOO_MANY_LOGIN_ATTEMPTS = "TOO_MANY_LOGIN_ATTEMPTS";

    // 500
    public static final String INTERNAL_SERVER_ERROR = "ERROR";

    // 501
    public static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
}
