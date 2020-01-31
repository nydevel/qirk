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

import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.model.cassandra.BaseChatMessage;
import org.wrkr.clb.chat.model.cassandra.DialogMessage;
import org.wrkr.clb.chat.model.cassandra.DialogMessage_;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

@Component
public class CassandraDialogMessageRepo extends BaseChatMessageRepo {

    private final static String CQL_SELECT_STATEMENT = String.format(
            "SELECT %s, %s, %s FROM %s WHERE %s = ? AND %s = ? AND %s < ? ORDER BY %s DESC LIMIT ?",
            DialogMessage_.senderId, DialogMessage_.timestamp, DialogMessage_.message, // select columns
            DialogMessage_.TABLE_NAME,
            DialogMessage_.user1Id, DialogMessage_.user2Id, DialogMessage_.timestamp, // filter columns
            DialogMessage_.timestamp); // order by column

    private final static String CQL_INSERT_STATEMENT = String.format(
            "INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?) IF NOT EXISTS",
            DialogMessage_.TABLE_NAME,
            DialogMessage_.uuid, DialogMessage_.user1Id, DialogMessage_.user2Id, DialogMessage_.senderId,
            DialogMessage_.timestamp, DialogMessage_.message);

    private PreparedStatement selectStatement;
    private PreparedStatement insertStatement;

    @Override
    public void afterPropertiesSet() throws Exception {
        selectStatement = session.prepare(CQL_SELECT_STATEMENT);
        insertStatement = session.prepare(CQL_INSERT_STATEMENT);
    }

    @Override
    protected PreparedStatement getSelectStatement() {
        return selectStatement;
    }

    @Override
    protected PreparedStatement getInsertStatement() {
        return insertStatement;
    }

    @Override
    protected BaseChatMessage createMessageInstance() {
        return new DialogMessage();
    }

    public List<BaseChatMessage> listTopSinceTimestampByChatId(long user1Id, long user2Id, long timestamp, int limit) {
        BoundStatement statement = getSelectStatement().bind(user1Id, user2Id, timestamp, limit);
        ResultSet resultSet = execute(statement);

        List<BaseChatMessage> messageList = new ArrayList<BaseChatMessage>(limit);
        for (Row row : resultSet) {
            DialogMessage message = (DialogMessage) createMessageInstance();

            message.setUser1Id(user1Id);
            message.setUser2Id(user2Id);
            message.setSenderId((Long) row.getObject(DialogMessage_.senderId));
            message.setTimestamp((Long) row.getObject(DialogMessage_.timestamp));
            message.setMessage(row.getString(DialogMessage_.message));

            messageList.add(message);
        }

        return messageList;
    }

    @Override
    public boolean save(BaseChatMessage message) {
        DialogMessage dialogueMessage = (DialogMessage) message;
        if (dialogueMessage.getUser1Id() > dialogueMessage.getUser2Id()) {
            throw new RuntimeException("User 1 id must be less than user 2 id");
        }

        BoundStatement statement = getInsertStatement().bind(dialogueMessage.getUuid(),
                dialogueMessage.getUser1Id(), dialogueMessage.getUser2Id(),
                dialogueMessage.getSenderId(), dialogueMessage.getTimestamp(), dialogueMessage.getMessage());
        ResultSet resultSet = execute(statement);

        return resultSet.wasApplied();
    }
}
