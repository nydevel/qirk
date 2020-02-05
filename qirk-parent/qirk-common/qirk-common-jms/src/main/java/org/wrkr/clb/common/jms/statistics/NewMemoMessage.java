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

public class NewMemoMessage extends BaseStatisticsMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(NewMemoMessage.class);

    public static final String AUTHOR_USER_ID = "author_user_id";
    public static final String CREATED_AT = "created_at";

    @JsonProperty(value = AUTHOR_USER_ID)
    public long authorUserId;
    @JsonProperty(value = CREATED_AT)
    public long createdAt;

    public NewMemoMessage(long authorUserId, long createdAt) {
        super(BaseStatisticsMessage.Code.NEW_MEMO);
        this.authorUserId = authorUserId;
        this.createdAt = createdAt;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
