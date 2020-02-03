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
package org.wrkr.clb.common.crypto.token.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NotificationSettingsTokenData {

    private static final ObjectWriter NOTIFICATION_TOKEN_DATA_WRITER = new ObjectMapper()
            .writerFor(NotificationSettingsTokenData.class);

    public static final String USER_EMAIL = "user_email";
    public static final String TYPE = "type";

    @JsonProperty(value = USER_EMAIL)
    public String userEmail;
    @JsonProperty(value = TYPE)
    public String type;

    public NotificationSettingsTokenData(String userEmail, String type) {
        this.userEmail = userEmail;
        this.type = type;
    }

    public String toJson() throws JsonProcessingException {
        return NOTIFICATION_TOKEN_DATA_WRITER.writeValueAsString(this);
    }
}
