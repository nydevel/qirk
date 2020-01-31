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
package org.wrkr.clb.common.crypto.token.crdt;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class CrdtTokenData {

    private static final ObjectWriter CRDT_TOKEN_DATA_WRITER = new ObjectMapper().writerFor(CrdtTokenData.class);

    @JsonIgnore
    public static final String USER_ID = "user_id";
    @JsonIgnore
    public static final String NOT_BEFORE = "not_before";
    @JsonIgnore
    public static final String NOT_ON_OR_AFTER = "not_after";

    @JsonProperty(value = USER_ID)
    public Long userId;
    @JsonProperty(value = NOT_BEFORE)
    public Long notBefore;
    @JsonProperty(value = NOT_ON_OR_AFTER)
    public Long notOnOrAfter;

    public CrdtTokenData() {
    }

    public CrdtTokenData(Map<String, Object> map) {
        userId = (Long) map.get(USER_ID);
        notBefore = (Long) map.get(NOT_BEFORE);
        notOnOrAfter = (Long) map.get(NOT_ON_OR_AFTER);
    }

    public String toJson() throws JsonProcessingException {
        return CRDT_TOKEN_DATA_WRITER.writeValueAsString(this);
    }
}
