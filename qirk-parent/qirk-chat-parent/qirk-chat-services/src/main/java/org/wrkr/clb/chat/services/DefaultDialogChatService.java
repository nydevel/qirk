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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.chat.services.sql.SQLChatService;
import org.wrkr.clb.chat.services.sql.SQLDialogChatService;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;
import org.wrkr.clb.common.jms.message.statistics.NewCommentMessage;

@Component
public class DefaultDialogChatService extends DefaultChatService implements DialogChatService {

    @Deprecated
    @Autowired(required = false)
    private SQLDialogChatService mariaDBService;

    @Deprecated
    @Override
    protected SQLChatService getSQLService() {
        return mariaDBService;
    }

    @Override
    public List<MessageDTO> getLastMessages(ChatTokenData tokenData, Long timestamp) {
        if (timestamp == null) {
            timestamp = System.currentTimeMillis();
        }

        long startTime = System.currentTimeMillis();
        List<MessageDTO> dtoList = getSQLService().getLastMessages(tokenData, timestamp);
        long resultTime = System.currentTimeMillis() - startTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug("processed method getLastMessages for chat type Dialog in cassandra in " + resultTime + " ms");
        }

        return dtoList;
    }

    @Override
    protected void deleteMessageFromMariaDB(ChatTokenData tokenData, long timestamp) {
        getSQLService().delete(tokenData, timestamp);
    }

    @Override
    protected void sendNewMessageStatistics(@SuppressWarnings("unused") ChatTokenData tokenData, BaseChatMessage messageEntity) {
        statisticsSender.send(new NewCommentMessage(
                messageEntity.getChatType(), messageEntity.getSenderId(), messageEntity.getTimestamp()));
    }
}
