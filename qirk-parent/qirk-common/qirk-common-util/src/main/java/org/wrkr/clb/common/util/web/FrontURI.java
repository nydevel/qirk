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
package org.wrkr.clb.common.util.web;

public class FrontURI {

    public static final String ACTIVATE_EMAIL_TOKEN = "/register?code=";
    public static final String RESET_PASSWORD = "/reset-password/";
    public static final String ACCEPT_INVITE = "/accept-email-invite?invite_key=";
    public static final String DECLINE_INVITE = "/decline-email-invite?invite_key=";
    public static final String MANAGE_SUBSCRIPTIONS = "/manage-subscriptions/";
    public static final String GET_TASK = "/project/%s/task/%d";

    public static String generateGetTaskURI(String remoteHost, String projectUiId, Long taskNumber) {
        return remoteHost + String.format(FrontURI.GET_TASK, projectUiId, taskNumber.toString());
    }
}
