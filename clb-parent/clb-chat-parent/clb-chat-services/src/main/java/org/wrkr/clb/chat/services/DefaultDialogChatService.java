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
package org.wrkr.clb.chat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.model.cassandra.BaseChatMessage;
import org.wrkr.clb.chat.services.cassandra.CassandraChatService;
import org.wrkr.clb.chat.services.cassandra.CassandraDialogChatService;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.chat.services.mariadb.MariaDBChatService;
import org.wrkr.clb.chat.services.mariadb.MariaDBDialogChatService;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;
import org.wrkr.clb.common.jms.statistics.NewMessageStatisticsMessage;


@Component
public class DefaultDialogChatService extends DefaultChatService implements DialogChatService {

    @Autowired
    private CassandraDialogChatService cassandraService;

    @Deprecated
    @Autowired(required = false)
    private MariaDBDialogChatService mariaDBService;

    @Override
    protected CassandraChatService getCassandraService() {
        return cassandraService;
    }

    @Deprecated
    @Override
    protected MariaDBChatService getMariaDBService() {
        return mariaDBService;
    }

    @Override
    public List<MessageDTO> getLastMessages(ChatTokenData tokenData, Long timestamp) {
        if (timestamp == null) {
            timestamp = System.currentTimeMillis();
        }

        long startTime = System.currentTimeMillis();
        List<MessageDTO> dtoList = getCassandraService().getLastMessages(tokenData, timestamp);
        long resultTime = System.currentTimeMillis() - startTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug("processed method getLastMessages for chat type Dialog in cassandra in " + resultTime + " ms");
        }

        return dtoList;
    }

    @Override
    protected void deleteMessageFromMariaDB(ChatTokenData tokenData, long timestamp) {
        getMariaDBService().delete(tokenData, timestamp);
    }

    @Override
    protected void sendNewMessageStatistics(@SuppressWarnings("unused") ChatTokenData tokenData, BaseChatMessage messageEntity) {
        statisticsSender.send(new NewMessageStatisticsMessage(
                messageEntity.getChatType(), messageEntity.getSenderId(), messageEntity.getTimestamp()));
    }
}
