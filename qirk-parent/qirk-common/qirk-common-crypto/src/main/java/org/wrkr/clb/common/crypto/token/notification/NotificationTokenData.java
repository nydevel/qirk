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
package org.wrkr.clb.common.crypto.token.notification;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NotificationTokenData {

    private static final ObjectWriter NOTIFICATION_TOKEN_DATA_WRITER = new ObjectMapper().writerFor(NotificationTokenData.class);

    public static final String SUBSCRIBER_ID = "subscriber_id";
    public static final String NOT_BEFORE = "not_before";
    public static final String NOT_ON_OR_AFTER = "not_after";

    @JsonProperty(value = SUBSCRIBER_ID)
    public Long subscriberId;
    @JsonProperty(value = NOT_BEFORE)
    public Long notBefore;
    @JsonProperty(value = NOT_ON_OR_AFTER)
    public Long notOnOrAfter;

    public NotificationTokenData() {
    }

    public NotificationTokenData(Map<String, Object> map) {
        subscriberId = (Long) map.get(SUBSCRIBER_ID);
        notBefore = (Long) map.get(NOT_BEFORE);
        notOnOrAfter = (Long) map.get(NOT_ON_OR_AFTER);
    }

    public String toJson() throws JsonProcessingException {
        return NOTIFICATION_TOKEN_DATA_WRITER.writeValueAsString(this);
    }
}
