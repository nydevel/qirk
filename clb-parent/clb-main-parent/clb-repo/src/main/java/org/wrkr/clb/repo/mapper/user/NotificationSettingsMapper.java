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
package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.NotificationSettingsMeta;

public class NotificationSettingsMapper extends BaseMapper<NotificationSettings> {

    public NotificationSettingsMapper() {
        super();
    }

    public NotificationSettingsMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(NotificationSettingsMeta.userId) + ", " +
                generateSelectColumnStatement(NotificationSettingsMeta.taskCreated) + ", " +
                generateSelectColumnStatement(NotificationSettingsMeta.taskUpdated) + ", " +
                generateSelectColumnStatement(NotificationSettingsMeta.taskCommented);
    }

    @Override
    public NotificationSettings mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        NotificationSettings settings = new NotificationSettings();

        settings.setUserId(rs.getLong(generateColumnAlias(NotificationSettingsMeta.userId)));
        settings.setTaskCreated(rs.getBoolean(generateColumnAlias(NotificationSettingsMeta.taskCreated)));
        settings.setTaskUpdated(rs.getBoolean(generateColumnAlias(NotificationSettingsMeta.taskUpdated)));
        settings.setTaskCommented(rs.getBoolean(generateColumnAlias(NotificationSettingsMeta.taskCommented)));

        return settings;
    }
}
