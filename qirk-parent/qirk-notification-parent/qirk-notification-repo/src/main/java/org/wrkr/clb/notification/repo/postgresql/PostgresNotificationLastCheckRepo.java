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

import org.springframework.stereotype.Repository;
import org.wrkr.clb.notification.model.NotificationLastCheck;
import org.wrkr.clb.notification.model.NotificationLastCheck_;
import org.wrkr.clb.notification.repo.NotificationLastCheckRepo;

@Repository
public class PostgresNotificationLastCheckRepo extends BaseNotifRepo implements NotificationLastCheckRepo {

    private static final String INSERT = "INSERT INTO " +
            NotificationLastCheck_.TABLE_NAME + " " +
            "(" + NotificationLastCheck_.subscriberId + ", " +
            NotificationLastCheck_.lastCheckTimestamp + ") " +
            "VALUES (?, ?);";

    private static final String EXISTS = "SELECT 1 " +
            "FROM " + NotificationLastCheck_.TABLE_NAME + " " +
            "WHERE " + NotificationLastCheck_.subscriberId + " = ?;"; // 1

    private static final String SELECT = "SELECT " + NotificationLastCheck_.lastCheckTimestamp + " " +
            "FROM " + NotificationLastCheck_.TABLE_NAME + " " +
            "WHERE " + NotificationLastCheck_.subscriberId + " = ?;"; // 1

    private static final String UPDATE = "UPDATE " + NotificationLastCheck_.TABLE_NAME + " " +
            "SET " + NotificationLastCheck_.lastCheckTimestamp + " = ? " + // 1
            "WHERE " + NotificationLastCheck_.subscriberId + " = ?;"; // 2

    @Override
    public boolean save(NotificationLastCheck lastCheck) {
        getJdbcTemplate().update(INSERT,
                lastCheck.getSubscriberId(), lastCheck.getLastCheckTimestamp());
        return true;
    }

    @Override
    public boolean exists(long subscriberId) {
        return exists(EXISTS, subscriberId);
    }

    @Override
    public Long getLastCheckTimestampBySubscriberId(Long subscriberId) {
        return queryForFirstObjectOrNull(SELECT, Long.class, subscriberId);
    }

    @Override
    public boolean update(NotificationLastCheck lastCheck) {
        updateSingleRow(UPDATE,
                lastCheck.getLastCheckTimestamp(), lastCheck.getSubscriberId());
        return true;
    }
}
