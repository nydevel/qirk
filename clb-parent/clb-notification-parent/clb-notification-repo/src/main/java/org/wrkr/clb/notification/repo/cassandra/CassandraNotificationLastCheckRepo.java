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
package org.wrkr.clb.notification.repo.cassandra;

import java.util.List;

import org.springframework.stereotype.Component;
import org.wrkr.clb.notification.model.NotificationLastCheck;
import org.wrkr.clb.notification.model.NotificationLastCheck_;
import org.wrkr.clb.notification.repo.NotificationLastCheckRepo;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

@Component
public class CassandraNotificationLastCheckRepo extends BaseNotifRepo implements NotificationLastCheckRepo {

    private static final String INSERT = "INSERT INTO " +
            NotificationLastCheck_.TABLE_NAME + " " +
            "(" + NotificationLastCheck_.subscriberId + ", " +
            NotificationLastCheck_.lastCheckTimestamp + ") " +
            "VALUES (?, ?) IF NOT EXISTS";

    private static final String EXISTS = "SELECT " + NotificationLastCheck_.subscriberId + " " +
            "FROM " + NotificationLastCheck_.TABLE_NAME + " " +
            "WHERE " + NotificationLastCheck_.subscriberId + " = ? LIMIT 1"; // 1

    private static final String SELECT = "SELECT " + NotificationLastCheck_.lastCheckTimestamp + " " +
            "FROM " + NotificationLastCheck_.TABLE_NAME + " " +
            "WHERE " + NotificationLastCheck_.subscriberId + " = ?"; // 1

    private static final String UPDATE = "UPDATE " +
            NotificationLastCheck_.TABLE_NAME + " " +
            "SET " + NotificationLastCheck_.lastCheckTimestamp + " = ? " + // 1
            "WHERE " + NotificationLastCheck_.subscriberId + " = ? IF EXISTS"; // 2

    private PreparedStatement insertStatement;
    private PreparedStatement existsStatement;
    private PreparedStatement selectStatement;
    private PreparedStatement updateStatement;

    @Override
    public void afterPropertiesSet() throws Exception {
        insertStatement = session.prepare(INSERT);
        existsStatement = session.prepare(EXISTS);
        selectStatement = session.prepare(SELECT);
        updateStatement = session.prepare(UPDATE);
    }

    @Override
    public boolean save(NotificationLastCheck lastCheck) {
        BoundStatement statement = insertStatement.bind(
                lastCheck.getSubscriberId(), lastCheck.getLastCheckTimestamp());
        ResultSet resultSet = session.execute(statement);
        return resultSet.wasApplied();
    }

    @Override
    public boolean exists(long subscriberId) {
        BoundStatement statement = existsStatement.bind(subscriberId);
        ResultSet resultSet = session.execute(statement);
        return !resultSet.all().isEmpty();
    }

    @Override
    public Long getLastCheckTimestampBySubscriberId(Long subscriberId) {
        BoundStatement statement = selectStatement.bind(subscriberId);
        ResultSet resultSet = session.execute(statement);
        List<Row> rows = resultSet.all();
        if (rows.isEmpty()) {
            return null;
        }
        return (Long) rows.get(0).getObject(NotificationLastCheck_.lastCheckTimestamp);
    }

    @Override
    public boolean update(NotificationLastCheck lastCheck) {
        BoundStatement statement = updateStatement.bind(
                lastCheck.getLastCheckTimestamp(), lastCheck.getSubscriberId());
        ResultSet resultSet = session.execute(statement);
        return resultSet.wasApplied();
    }
}
