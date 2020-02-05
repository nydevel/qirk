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

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class UserRegisteredMessage extends BaseStatisticsMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(UserRegisteredMessage.class);

    public static final String UUID = "uuid";
    public static final String USER_ID = "user_id";
    public static final String REGISTERED_AT = "registered_at";

    @JsonProperty(value = UUID)
    public String uuid;
    @JsonProperty(value = USER_ID)
    public long userId;
    @JsonProperty(value = REGISTERED_AT)
    public long registeredAt;

    public UserRegisteredMessage(String uuid, long userId, OffsetDateTime registeredAt) {
        super(Code.USER_REGISTERED);
        this.uuid = uuid;
        this.userId = userId;
        this.registeredAt = registeredAt.toInstant().toEpochMilli();
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
