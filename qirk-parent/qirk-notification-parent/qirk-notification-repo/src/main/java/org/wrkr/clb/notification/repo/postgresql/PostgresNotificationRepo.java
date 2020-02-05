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
package org.wrkr.clb.notification.repo.postgresql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.notification.model.Notification_;
import org.wrkr.clb.notification.repo.NotificationRepo;
import org.wrkr.clb.notification.repo.dto.NotificationDTO;

@Repository
public class PostgresNotificationRepo extends BaseNotifRepo implements NotificationRepo {

    private static final String INSERT = "INSERT INTO " +
            Notification_.TABLE_NAME + " " +
            "(" + Notification_.subscriberId + ", " +
            Notification_.timestamp + ", " +
            Notification_.notificationType + ", " +
            Notification_.json + ") " +
            "VALUES (?, ?, ?, ?);";

    private static final String SELECT = "SELECT " +
            Notification_.timestamp + ", " +
            Notification_.notificationType + ", " +
            Notification_.json + " " +
            "FROM " + Notification_.TABLE_NAME + " " +
            "WHERE " + Notification_.subscriberId + " = ? " +
            "AND " + Notification_.timestamp + " < ? " +
            "ORDER BY " + Notification_.timestamp + " DESC " +
            "LIMIT ?;";

    @Override
    public boolean save(long subscriberId, long timestamp, String notificationType, String json) {
        try {
            getJdbcTemplate().update(INSERT, subscriberId, timestamp, notificationType, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<NotificationDTO> listTopSinceTimestampBySubscriberId(long subscriberId, long timestamp, int limit) {
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(SELECT, subscriberId, timestamp, limit);

        List<NotificationDTO> dtoList = new ArrayList<NotificationDTO>(rows.size());
        for (Map<String, Object> row : rows) {
            dtoList.add(NotificationDTO.fromRow(row));
        }

        return dtoList;
    }
}
