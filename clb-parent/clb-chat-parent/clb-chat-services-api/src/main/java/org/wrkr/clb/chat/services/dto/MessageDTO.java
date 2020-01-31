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
package org.wrkr.clb.chat.services.dto;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.common.util.datetime.DateTimeWithEpochDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDTO {

    @JsonIgnore
    public static final String CHAT_TYPE = "chat_type";
    @JsonIgnore
    public static final String CHAT_ID = "chat_id";

    @JsonProperty(value = "sender_id")
    public Long senderId;

    public DateTimeWithEpochDTO timestamp;

    public String message;

    @JsonProperty(CHAT_TYPE)
    @JsonInclude(Include.NON_NULL)
    public String chatType;

    @JsonProperty(CHAT_ID)
    @JsonInclude(Include.NON_NULL)
    public Long chatId;

    @JsonProperty(value = "external_uuid")
    @JsonInclude(Include.NON_NULL)
    public String externalUuid;

    public static MessageDTO fromEntity(org.wrkr.clb.chat.model.mariadb.BaseChatMessage message) {
        MessageDTO dto = new MessageDTO();

        dto.senderId = message.getSenderId();
        dto.timestamp = new DateTimeWithEpochDTO(message.getTimestamp());
        dto.message = message.getMessage();

        return dto;
    }

    public static MessageDTO fromEntityWithChat(org.wrkr.clb.chat.model.mariadb.BaseAttachedChatMessage message) {
        MessageDTO dto = fromEntity(message);
        dto.chatType = message.getChatType();
        dto.chatId = message.getChatId();
        return dto;
    }

    public static MessageDTO fromEntityWithChat(org.wrkr.clb.chat.model.mariadb.DialogMessage message) {
        MessageDTO dto = fromEntity(message);
        dto.chatType = message.getChatType();
        if (message.getSenderId() != message.getUser1Id()) {
            dto.chatId = message.getUser1Id();
        } else if (message.getSenderId() != message.getUser2Id()) {
            dto.chatId = message.getUser2Id();
        }
        return dto;
    }

    public static <M extends org.wrkr.clb.chat.model.mariadb.BaseChatMessage> List<MessageDTO> fromMariaDBEntities(
            List<M> messageList) {
        List<MessageDTO> dtoList = new ArrayList<MessageDTO>(messageList.size());
        for (org.wrkr.clb.chat.model.mariadb.BaseChatMessage message : messageList) {
            dtoList.add(fromEntity(message));
        }
        return dtoList;
    }

    public static MessageDTO fromEntity(org.wrkr.clb.chat.model.cassandra.BaseChatMessage message) {
        MessageDTO dto = new MessageDTO();

        dto.senderId = message.getSenderId();
        dto.timestamp = new DateTimeWithEpochDTO(message.getTimestamp());
        dto.message = message.getMessage();

        return dto;
    }

    public static MessageDTO fromEntityWithChat(org.wrkr.clb.chat.model.cassandra.BaseAttachedChatMessage message) {
        MessageDTO dto = fromEntity(message);
        dto.chatType = message.getChatType();
        dto.chatId = message.getChatId();
        return dto;
    }

    public static MessageDTO fromEntityWithChat(org.wrkr.clb.chat.model.cassandra.DialogMessage message) {
        MessageDTO dto = fromEntity(message);
        dto.chatType = message.getChatType();
        if (message.getSenderId() != message.getUser1Id()) {
            dto.chatId = message.getUser1Id();
        } else if (message.getSenderId() != message.getUser2Id()) {
            dto.chatId = message.getUser2Id();
        }
        return dto;
    }

    public static <M extends org.wrkr.clb.chat.model.cassandra.BaseChatMessage> List<MessageDTO> fromCassandraEntities(
            List<M> messageList) {
        List<MessageDTO> dtoList = new ArrayList<MessageDTO>(messageList.size());
        for (org.wrkr.clb.chat.model.cassandra.BaseChatMessage message : messageList) {
            dtoList.add(fromEntity(message));
        }
        return dtoList;
    }
}
