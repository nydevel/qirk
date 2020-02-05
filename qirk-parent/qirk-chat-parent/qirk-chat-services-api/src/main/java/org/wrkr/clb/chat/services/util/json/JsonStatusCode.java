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
package org.wrkr.clb.chat.services.util.json;

public class JsonStatusCode {

    public static final String OK = "OK";

    public static final String INVALID_REQUEST_TYPE = "INVALID_REQUEST_TYPE";
    public static final String INVALID_CHAT_TYPE = "INVALID_CHAT_TYPE";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String INVALID_TOKEN_FOR_WRITE = "INVALID_TOKEN_FOR_WRITE";
    public static final String INVALID_TOKEN_CHAT_TYPE = "TOKEN_INVALID_CHAT_TYPE";

    public static final String INTERNAL_SERVER_ERROR = "ERROR";
}
