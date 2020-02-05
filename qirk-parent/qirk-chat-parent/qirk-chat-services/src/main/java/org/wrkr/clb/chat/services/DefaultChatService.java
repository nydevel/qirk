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
package org.wrkr.clb.chat.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.services.dto.ChatWithLastMessageDTO;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.chat.services.sql.SQLChatService;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;
import org.wrkr.clb.common.jms.message.statistics.NewMessageStatisticsMessage;
import org.wrkr.clb.common.jms.services.StatisticsSender;

public abstract class DefaultChatService implements ChatService {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    protected StatisticsSender statisticsSender;

    protected abstract SQLChatService getSQLService();

    @Override
    public List<MessageDTO> getLastMessages(ChatTokenData tokenData, Long timestamp) {
        if (timestamp == null) {
            timestamp = System.currentTimeMillis();
        }

        long startTime = System.currentTimeMillis();
        List<MessageDTO> dtoList = getSQLService().getLastMessages(tokenData, timestamp);
        long resultTime = System.currentTimeMillis() - startTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug("processed method getLastMessages for chat type " + tokenData.chatType + " in cassandra in " +
                    resultTime + " ms");
        }

        return dtoList;
    }

    protected void deleteMessageFromMariaDB(ChatTokenData tokenData, long timestamp) {
        getSQLService().delete(tokenData, timestamp);
    }

    protected void sendNewMessageStatistics(ChatTokenData tokenData, BaseChatMessage messageEntity) {
        statisticsSender.send(new NewMessageStatisticsMessage(
                messageEntity.getChatType(), tokenData.chatId, messageEntity.getTimestamp()));
    }

    @Override
    public MessageDTO saveMessage(ChatTokenData tokenData, String message) throws SQLException {
        UUID uuid = UUID.randomUUID();

        BaseChatMessage messageEntity = getSQLService().saveMessage(tokenData, message, uuid);
        sendNewMessageStatistics(tokenData, messageEntity);

        return MessageDTO.fromEntity(messageEntity);
    }

    @Override
    public List<ChatWithLastMessageDTO> getChatList(List<Long> chatIds) {
        if (chatIds.isEmpty()) {
            return new ArrayList<ChatWithLastMessageDTO>();
        }

        return getSQLService().getChatList(chatIds);
    }
}
