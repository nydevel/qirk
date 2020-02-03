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
package org.wrkr.clb.common.crypto.token.chat;

import org.wrkr.clb.common.util.strings.JsonUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class SecurityTokenData {

    @JsonIgnore
    public static final String SENDER_ID = "sender_id";
    @JsonIgnore
    public static final String WRITE = "write";
    @JsonIgnore
    public static final String NOT_BEFORE = "not_before";
    @JsonIgnore
    public static final String NOT_ON_OR_AFTER = "not_after";

    @JsonProperty(value = SENDER_ID)
    public Long senderId;
    @JsonProperty(value = WRITE)
    public boolean write = false;
    @JsonProperty(value = NOT_BEFORE)
    public Long notBefore;
    @JsonProperty(value = NOT_ON_OR_AFTER)
    public Long notOnOrAfter;

    public String toJson() throws JsonProcessingException {
        return JsonUtils.convertObjectToJson(this);
    }
}
