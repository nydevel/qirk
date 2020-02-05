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
package org.wrkr.clb.common.jms.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NewUserMessage extends BaseStatisticsMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(NewUserMessage.class);

    public static final String UUID = "uuid";
    public static final String VISITED_AT = "visited_at";

    @JsonProperty(value = UUID)
    public String uuid;
    @JsonProperty(value = VISITED_AT)
    public long visitedAt;

    public NewUserMessage(String uuid, long visitedAt) {
        super(Code.NEW_USER);
        this.uuid = uuid;
        this.visitedAt = visitedAt;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
