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
package org.wrkr.clb.chat.repo.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage_;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.model.sql.BaseChatMessage_;
import org.wrkr.clb.chat.model.sql.DialogMessage;
import org.wrkr.clb.chat.model.sql.DialogMessage_;
import org.wrkr.clb.chat.model.sql.IssueMessage_;

@Repository
public class SQLDialogMessageRepo extends BaseChatMessageRepo {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(SQLTaskMessageRepo.class);

    private static final String SQL_SELECT_MESSAGES_STATEMENT = String.format(
            "SELECT %s, %s, %s, %s FROM %s WHERE %s = ? AND %s = ? AND %s < ? ORDER BY %s DESC LIMIT ?;",
            DialogMessage_.user1Id, DialogMessage_.user2Id, BaseChatMessage_.timestamp, BaseChatMessage_.message, // select
                                                                                                                  // columns
            DialogMessage_.TABLE_NAME, // table name
            DialogMessage_.user1Id, DialogMessage_.user2Id, BaseChatMessage_.timestamp, // where clause
            BaseChatMessage_.timestamp); // order by

    private static final String SQL_INSERT_STATEMENT = String.format(
            "INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);", DialogMessage_.TABLE_NAME,
            BaseChatMessage_.uuid, DialogMessage_.user1Id, DialogMessage_.user2Id, BaseChatMessage_.senderId,
            BaseChatMessage_.timestamp,
            BaseChatMessage_.message);

    private static final String SQL_DELETE_STATEMENT = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?;",
            DialogMessage_.TABLE_NAME, DialogMessage_.user1Id, DialogMessage_.user2Id);

    private static final String SQL_SELECT_CHATS_STATEMENT = String.format("SELECT %s.%s, %s, %s, %s FROM "
            + "(SELECT %s, MAX(%s) as last_timestamp FROM %s WHERE %s in (?) GROUP BY %s) AS last_message "
            + "JOIN %s ON last_message.%s = %s.%s AND last_message.last_timestamp = %s.%s ORDER BY last_timestamp DESC;",
            IssueMessage_.TABLE_NAME, BaseAttachedChatMessage_.chatId, BaseAttachedChatMessage_.senderId,
            BaseChatMessage_.timestamp, BaseChatMessage_.message, // select columns
            BaseAttachedChatMessage_.chatId, BaseChatMessage_.timestamp, // subquery select columns
            IssueMessage_.TABLE_NAME, // subquery table name
            BaseAttachedChatMessage_.chatId, // subquery where clause
            BaseAttachedChatMessage_.chatId, // subquery group by clause
            IssueMessage_.TABLE_NAME, // join table name
            BaseAttachedChatMessage_.chatId, IssueMessage_.TABLE_NAME, BaseAttachedChatMessage_.chatId, // join clause
            IssueMessage_.TABLE_NAME, BaseChatMessage_.timestamp); // join clause

    protected DialogMessage createMessageInstance() {
        return new DialogMessage();
    }

    @Override
    protected String getSqlSelectMessagesStatement() {
        return SQL_SELECT_MESSAGES_STATEMENT;
    }

    @Override
    protected String getSqlInsertStatement() {
        return SQL_INSERT_STATEMENT;
    }

    @Override
    protected String getSqlDeleteStatement() {
        return SQL_DELETE_STATEMENT;
    }

    @Override
    protected String getSqlSelectChatsStatement() {
        return SQL_SELECT_CHATS_STATEMENT;
    }

    public List<BaseChatMessage> listTopSinceTimestampByUserId(long user1Id, long user2Id, long timestamp, int limit) {
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(getSqlSelectMessagesStatement(),
                user1Id, user2Id, timestamp, limit);

        List<BaseChatMessage> messageList = new ArrayList<BaseChatMessage>(limit);
        for (Map<String, Object> row : rows) {
            DialogMessage message = createMessageInstance();
            message.setUser1Id(user1Id);
            message.setUser2Id(user2Id);
            message.setTimestamp((Long) row.get(BaseChatMessage_.timestamp));
            message.setMessage((String) row.get(BaseChatMessage_.message));
            messageList.add(message);
        }

        return messageList;
    }

    @Override
    public void save(BaseChatMessage message) {
        DialogMessage dialogueMessage = (DialogMessage) message;
        if (dialogueMessage.getUser1Id() > dialogueMessage.getUser2Id()) {
            throw new RuntimeException("User 1 id must be less than user 2 id");
        }

        getJdbcTemplate().update(getSqlInsertStatement(), dialogueMessage.getUuid(),
                dialogueMessage.getUser1Id(), dialogueMessage.getUser2Id(),
                dialogueMessage.getSenderId(), dialogueMessage.getTimestamp(), dialogueMessage.getMessage());
    }

    public void deleteByUserIdAndTimestamp(long user1Id, long user2Id, long timestamp) {
        getJdbcTemplate().update(getSqlDeleteStatement(), user1Id, user2Id, timestamp);
    }

    // TODO
    @Deprecated
    @SuppressWarnings("unused")
    public List<DialogMessage> getChatList(List<Long> chatIds) {
        List<DialogMessage> messageList = new ArrayList<DialogMessage>();
        /*@formatter:off
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(getSqlSelectChatsStatement(),
                StringUtils.join(dialogIds, ", "));
        for (Map<String, Object> row : rows) {
            DialogMessage message = new DialogMessage();
            message.setUser1Id((Long) row.get(DialogMessage_.user1Id));
            message.setUser2Id((Long) row.get(DialogMessage_.user2Id));
            message.setTimestamp((Long) row.get(BaseChatMessage_.timestamp));
            message.setMessage((String) row.get(BaseChatMessage_.message));
            messageList.add(message);
        }
        @formatter:on*/
        return messageList;
    }
}
