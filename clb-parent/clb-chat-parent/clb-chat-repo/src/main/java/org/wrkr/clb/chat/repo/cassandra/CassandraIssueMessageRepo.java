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

import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.model.cassandra.BaseChatMessage;
import org.wrkr.clb.chat.model.cassandra.IssueMessage;
import org.wrkr.clb.chat.model.cassandra.IssueMessage_;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;


@Component
public class CassandraIssueMessageRepo extends BaseAttachedChatMessageRepo {

    private static final String CQL_SELECT_STATEMENT = String.format(
            "SELECT %s, %s, %s FROM %s WHERE %s = ? AND %s < ? ORDER BY %s DESC LIMIT ?",
            IssueMessage_.senderId, IssueMessage_.timestamp, IssueMessage_.message, // select columns
            IssueMessage_.TABLE_NAME,
            IssueMessage_.chatId, IssueMessage_.timestamp, // filter columns
            IssueMessage_.timestamp); // order by column

    private static final String CQL_INSERT_STATEMENT = String.format(
            "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?) IF NOT EXISTS",
            IssueMessage_.TABLE_NAME,
            IssueMessage_.uuid, IssueMessage_.chatId, IssueMessage_.senderId, IssueMessage_.timestamp, IssueMessage_.message);

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
        return new IssueMessage();
    }
}
