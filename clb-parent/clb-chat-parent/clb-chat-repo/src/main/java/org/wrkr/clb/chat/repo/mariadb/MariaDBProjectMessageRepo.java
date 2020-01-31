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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.chat.model.cassandra.BaseChatMessage_;
import org.wrkr.clb.chat.model.mariadb.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.mariadb.BaseAttachedChatMessage_;
import org.wrkr.clb.chat.model.mariadb.ProjectMessage;
import org.wrkr.clb.chat.model.mariadb.ProjectMessage_;


@Repository
public class MariaDBProjectMessageRepo extends BaseAttachedChatMessageRepo {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MariaDBProjectMessageRepo.class);

    private static final String SQL_SELECT_MESSAGES_STATEMENT = String.format(
            "SELECT %s, %s, %s FROM %s WHERE %s = ? AND %s < ? ORDER BY %s DESC LIMIT ?;",
            BaseAttachedChatMessage_.senderId, BaseChatMessage_.timestamp, BaseChatMessage_.message, // select columns
            ProjectMessage_.TABLE_NAME,
            BaseAttachedChatMessage_.chatId, BaseChatMessage_.timestamp, // filter columns
            BaseChatMessage_.timestamp); // order by

    private static final String SQL_INSERT_STATEMENT = String.format(
            "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?);",
            ProjectMessage_.TABLE_NAME,
            BaseChatMessage_.uuid, BaseAttachedChatMessage_.chatId, BaseAttachedChatMessage_.senderId, BaseChatMessage_.timestamp,
            BaseChatMessage_.message);

    private static final String SQL_DELETE_STATEMENT = String.format(
            "DELETE FROM %s WHERE %s = ? AND %s = ?;",
            ProjectMessage_.TABLE_NAME,
            BaseAttachedChatMessage_.chatId, BaseChatMessage_.timestamp);

    private static final String SQL_SELECT_CHATS_STATEMENT = String.format(
            "SELECT %s.%s, %s, %s, %s FROM "
                    + "(SELECT %s, MAX(%s) as last_timestamp FROM %s WHERE %s in (?) GROUP BY %s) AS last_message "
                    + "JOIN %s ON last_message.%s = %s.%s AND last_message.last_timestamp = %s.%s ORDER BY last_timestamp DESC;",
            ProjectMessage_.TABLE_NAME,
            BaseAttachedChatMessage_.chatId, BaseAttachedChatMessage_.senderId, BaseChatMessage_.timestamp, BaseChatMessage_.message, // select
                                                                                                                  // columns
            BaseAttachedChatMessage_.chatId, BaseChatMessage_.timestamp, // subquery select columns
            ProjectMessage_.TABLE_NAME, // subquery table name
            BaseAttachedChatMessage_.chatId, // subquery where clause
            BaseAttachedChatMessage_.chatId, // subquery group by clause
            ProjectMessage_.TABLE_NAME, // join table name
            BaseAttachedChatMessage_.chatId, ProjectMessage_.TABLE_NAME, BaseAttachedChatMessage_.chatId, // join clause
            ProjectMessage_.TABLE_NAME, BaseChatMessage_.timestamp); // join clause

    @Override
    protected BaseAttachedChatMessage createMessageInstance() {
        return new ProjectMessage();
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
}
