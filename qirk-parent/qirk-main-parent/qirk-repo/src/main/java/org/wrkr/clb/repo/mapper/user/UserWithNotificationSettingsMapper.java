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
package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.user.User;

public class UserWithNotificationSettingsMapper extends UserMapper {

    private NotificationSettingsMapper notificationSettingsMapper;

    public UserWithNotificationSettingsMapper(String userTableName, String notifSettingsTableName) {
        super(userTableName);
        notificationSettingsMapper = new NotificationSettingsMapper(notifSettingsTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                notificationSettingsMapper.generateSelectColumnsStatement();
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);
        user.setNotificationSettings(notificationSettingsMapper.mapRow(rs, rowNum));
        return user;
    }
}