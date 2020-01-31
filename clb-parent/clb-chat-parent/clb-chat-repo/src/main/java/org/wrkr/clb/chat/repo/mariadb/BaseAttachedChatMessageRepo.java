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
package org.wrkr.clb.chat.repo.mariadb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wrkr.clb.chat.model.mariadb.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.mariadb.BaseAttachedChatMessage_;
import org.wrkr.clb.chat.model.mariadb.BaseChatMessage;
import org.wrkr.clb.chat.model.mariadb.BaseChatMessage_;

public abstract class BaseAttachedChatMessageRepo extends BaseChatMessageRepo {

    public List<BaseChatMessage> listTopSinceTimestampByChatId(long chatId, long timestamp, int limit) {
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(getSqlSelectMessagesStatement(),
                chatId, timestamp, limit);

        List<BaseChatMessage> messageList = new ArrayList<BaseChatMessage>(limit);
        for (Map<String, Object> row : rows) {
            BaseAttachedChatMessage message = createMessageInstance();
            message.setChatId(chatId);
            message.setSenderId((Long) row.get(BaseAttachedChatMessage_.senderId));
            message.setTimestamp((Long) row.get(BaseChatMessage_.timestamp));
            message.setMessage((String) row.get(BaseChatMessage_.message));
            messageList.add(message);
        }

        return messageList;
    }

    @Override
    public void save(BaseChatMessage message) {
        BaseAttachedChatMessage attachedChatMessage = (BaseAttachedChatMessage) message;
        getJdbcTemplate().update(getSqlInsertStatement(), attachedChatMessage.getUuid(),
                attachedChatMessage.getChatId(), attachedChatMessage.getSenderId(), attachedChatMessage.getTimestamp(),
                attachedChatMessage.getMessage());
    }

    public void deleteByChatIdAndTimestamp(long chatId, long timestamp) {
        getJdbcTemplate().update(getSqlDeleteStatement(), chatId, timestamp);
    }

    // TODO
    @Deprecated
    @SuppressWarnings("unused")
    public List<BaseAttachedChatMessage> getChatList(List<Long> chatIds) {
        List<BaseAttachedChatMessage> messageList = new ArrayList<BaseAttachedChatMessage>();
        // @formatter:off
        /*List<Map<String, Object>> rows = getJdbcTemplate().queryForList(getSqlSelectChatsStatement(),
                StringUtils.join(chatIds, ", "));
        for (Map<String, Object> row : rows) {
            BaseAttachedChatMessage message = createMessageInstance();
            message.setChatId((Long) row.get(BaseAttachedChatMessage_.chatId));
            message.setSenderId((Long) row.get(BaseAttachedChatMessage_.senderId));
            message.setTimestamp((Long) row.get(BaseChatMessage_.timestamp));
            message.setMessage((String) row.get(BaseChatMessage_.message));
            messageList.add(message);
        }*/
        // @formatter:on
        return messageList;
    }

    protected abstract BaseAttachedChatMessage createMessageInstance();

}
