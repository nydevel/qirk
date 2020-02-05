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
package org.wrkr.clb.common.jms.message.statistics;

import org.wrkr.clb.common.util.strings.JSONifiable;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseStatisticsMessage implements JSONifiable {

    public static class Code {
        public static final String NEW_USER = "STAT_NEW_USER";
        public static final String USER_REGISTERED = "STAT_USER_REGISTERED";
        public static final String FIRST_LOGIN = "STAT_FIRST_LOGIN";
        public static final String REMEMBER_ME_LOGIN = "STAT_REMEMBER_ME_LOGIN";
        public static final String NOTIFICATION_UNSUBSCRIPTION = "STAT_NOTIFICATION_UNSUBSCRIPTION";

        public static final String PROJECT_DOC_UPDATE = "STAT_PROJECT_DOC_UPDATE";
        public static final String NEW_TASK = "STAT_NEW_TASK";
        public static final String TASK_UPDATE = "STAT_TASK_UPDATE";
        public static final String NEW_MEMO = "STAT_NEW_MEMO";

        public static final String NEW_MESSAGE = "STAT_NEW_MESSAGE";
    }

    public static final String CODE = "code";

    @JsonProperty(value = CODE)
    public String code;

    public BaseStatisticsMessage(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return getClass() + " [code=" + code + "]";
    }
}
