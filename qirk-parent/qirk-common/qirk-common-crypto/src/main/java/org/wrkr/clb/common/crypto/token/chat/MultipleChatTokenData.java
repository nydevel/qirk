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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class MultipleChatTokenData extends SecurityTokenData {

    private static final ObjectWriter MULTIPLE_CHAT_TOKEN_DATA_WRITER = new ObjectMapper().writerFor(MultipleChatTokenData.class);

    @JsonIgnore
    public static final String CHATS = "chats";
    @JsonProperty(value = CHATS)
    public Map<String, List<Long>> chatTypeToChatIds;

    public MultipleChatTokenData() {
    }

    @SuppressWarnings("unchecked")
    public MultipleChatTokenData(Map<String, Object> map) {
        chatTypeToChatIds = (Map<String, List<Long>>) map.get(CHATS);
        senderId = (Long) map.get(SENDER_ID);
        write = (Boolean) map.get(WRITE);
        notBefore = (Long) map.get(NOT_BEFORE);
        notOnOrAfter = (Long) map.get(NOT_ON_OR_AFTER);
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MULTIPLE_CHAT_TOKEN_DATA_WRITER.writeValueAsString(this);
    }

    public void addChats(String chatType, List<Long> chatIds) {
        chatTypeToChatIds.put(chatType, chatIds);
    }
}
