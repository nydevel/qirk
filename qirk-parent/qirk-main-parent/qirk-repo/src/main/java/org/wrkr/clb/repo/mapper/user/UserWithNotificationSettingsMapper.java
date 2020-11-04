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