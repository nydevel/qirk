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
package org.wrkr.clb.chat.repo.cassandra;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.chat.model.cassandra.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.cassandra.BaseChatMessage;
import org.wrkr.clb.chat.model.cassandra.BaseChatMessage_;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public abstract class BaseAttachedChatMessageRepo extends BaseChatMessageRepo {

    public List<BaseChatMessage> listTopSinceTimestampByChatId(long chatId, long timestamp, int limit) {
        BoundStatement statement = getSelectStatement().bind(chatId, timestamp, limit);
        ResultSet resultSet = execute(statement);

        List<BaseChatMessage> messageList = new ArrayList<BaseChatMessage>(limit);
        for (Row row : resultSet) {
            BaseAttachedChatMessage message = (BaseAttachedChatMessage) createMessageInstance();

            message.setChatId(chatId);
            message.setSenderId((Long) row.getObject(BaseChatMessage_.senderId));
            message.setTimestamp((Long) row.getObject(BaseChatMessage_.timestamp));
            message.setMessage(row.getString(BaseChatMessage_.message));

            messageList.add(message);
        }

        return messageList;
    }

    @Override
    public boolean save(BaseChatMessage message) {
        BaseAttachedChatMessage attachedMessage = (BaseAttachedChatMessage) message;
        BoundStatement statement = getInsertStatement().bind(
                attachedMessage.getUuid(), attachedMessage.getChatId(), attachedMessage.getSenderId(),
                attachedMessage.getTimestamp(), attachedMessage.getMessage());
        ResultSet resultSet = execute(statement);

        return resultSet.wasApplied();
    }
}
