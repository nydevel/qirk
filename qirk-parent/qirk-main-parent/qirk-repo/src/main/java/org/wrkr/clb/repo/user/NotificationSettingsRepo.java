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
package org.wrkr.clb.repo.user;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.NotificationSettingsMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.user.NotificationSettingsMapper;

@Repository
public class NotificationSettingsRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + NotificationSettingsMeta.TABLE_NAME + " " +
            "(" + NotificationSettingsMeta.userId + ", " + // 1
            NotificationSettingsMeta.taskCreated + ", " + // 2
            NotificationSettingsMeta.taskUpdated + ", " + // 3
            NotificationSettingsMeta.taskCommented + ") " + // 4
            "VALUES (?, ?, ?, ?);";

    private static final String UPDATE = "UPDATE " + NotificationSettingsMeta.TABLE_NAME + " " +
            "SET " + NotificationSettingsMeta.taskCreated + " = ?, " + // 1
            NotificationSettingsMeta.taskUpdated + " = ?, " + // 2
            NotificationSettingsMeta.taskCommented + " = ? " + // 3
            "WHERE " + NotificationSettingsMeta.userId + " = ?;";// 4

    private static final String EXISTS_BY_USER_ID = "SELECT 1 " +
            "FROM " + NotificationSettingsMeta.TABLE_NAME + " " +
            "WHERE " + NotificationSettingsMeta.userId + " = ?;"; // 1

    private static final NotificationSettingsMapper NOTIFICATION_SETTINGS_MAPPER = new NotificationSettingsMapper();

    private static final String SELECT_BY_USER_ID = "SELECT " +
            NOTIFICATION_SETTINGS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + NotificationSettingsMeta.TABLE_NAME + " " +
            "WHERE " + NotificationSettingsMeta.userId + " = ?;"; // 1

    private static final String SELECT_BY_USER_EMAIL = "SELECT " +
            NOTIFICATION_SETTINGS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + NotificationSettingsMeta.TABLE_NAME + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + NotificationSettingsMeta.userId + " = " + UserMeta.id + " " +
            "WHERE " + UserMeta.emailAddress + " = ?;"; // 1

    public void save(NotificationSettings notifSettings) {
        getJdbcTemplate().update(INSERT,
                notifSettings.getUserId(),
                notifSettings.isTaskCreated(), notifSettings.isTaskUpdated(), notifSettings.isTaskCommented());
    }

    public void update(NotificationSettings notifSettings) {
        getJdbcTemplate().update(UPDATE,
                notifSettings.isTaskCreated(), notifSettings.isTaskUpdated(), notifSettings.isTaskCommented(),
                notifSettings.getUserId());
    }

    public boolean exists(Long userId) {
        return exists(EXISTS_BY_USER_ID, userId);
    }

    public NotificationSettings getByUserId(Long userId) {
        return queryForObjectOrNull(SELECT_BY_USER_ID, NOTIFICATION_SETTINGS_MAPPER, userId);
    }

    public NotificationSettings getByUserEmail(String userEmail) {
        return queryForObjectOrNull(SELECT_BY_USER_EMAIL, NOTIFICATION_SETTINGS_MAPPER, userEmail);
    }
}
