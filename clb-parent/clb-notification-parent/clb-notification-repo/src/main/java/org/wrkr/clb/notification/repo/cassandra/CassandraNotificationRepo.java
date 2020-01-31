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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.wrkr.clb.notification.model.Notification_;
import org.wrkr.clb.notification.repo.NotificationRepo;
import org.wrkr.clb.notification.repo.dto.NotificationDTO;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

@Component
public class CassandraNotificationRepo extends BaseNotifRepo implements NotificationRepo {

    private static final String INSERT = "INSERT INTO " +
            Notification_.TABLE_NAME + " " +
            "(" + Notification_.subscriberId + ", " +
            Notification_.timestamp + ", " +
            Notification_.notificationType + ", " +
            Notification_.json + ") " +
            "VALUES (?, ?, ?, ?) IF NOT EXISTS";

    private static final String SELECT = "SELECT " +
            Notification_.timestamp + ", " +
            Notification_.notificationType + ", " +
            Notification_.json + " " +
            "FROM " + Notification_.TABLE_NAME + " " +
            "WHERE " + Notification_.subscriberId + " = ? " +
            "AND " + Notification_.timestamp + " < ? " +
            "ORDER BY " + Notification_.timestamp + " DESC " +
            "LIMIT ?";

    private PreparedStatement insertStatement;
    private PreparedStatement selectStatement;

    @Override
    public void afterPropertiesSet() throws Exception {
        insertStatement = session.prepare(INSERT);
        selectStatement = session.prepare(SELECT);
    }

    @Override
    public boolean save(long subscriberId, long timestamp, String notificationType, String json) {
        BoundStatement statement = insertStatement.bind(subscriberId, timestamp, notificationType, json);
        ResultSet resultSet = session.execute(statement);
        return resultSet.wasApplied();
    }

    @Override
    public List<NotificationDTO> listTopSinceTimestampBySubscriberId(long subscriberId, long timestamp, int limit) {
        BoundStatement statement = selectStatement.bind(subscriberId, timestamp, limit);
        ResultSet resultSet = session.execute(statement);

        List<Row> rows = resultSet.all();
        List<NotificationDTO> dtoList = new ArrayList<NotificationDTO>(rows.size());
        for (Row row : rows) {
            dtoList.add(NotificationDTO.fromRow(row));
        }

        return dtoList;
    }
}
