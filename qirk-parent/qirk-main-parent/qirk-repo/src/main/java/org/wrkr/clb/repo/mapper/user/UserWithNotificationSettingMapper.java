package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;

public class UserWithNotificationSettingMapper extends BaseUserMapper {

    protected String notifSettingsTableName;
    private String notifSetting;

    public UserWithNotificationSettingMapper(String userTableName, String notifSettingsTableName, String notifSetting) {
        super(userTableName);
        this.notifSettingsTableName = notifSettingsTableName;
        this.notifSetting = notifSetting;
    }

    public String generateNotifSettingColumnAlias() {
        return notifSettingsTableName + "__" + notifSetting;
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(UserMeta.emailAddress) + ", " +
                notifSettingsTableName + "." + notifSetting + " AS " + generateNotifSettingColumnAlias();
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);

        user.setEmailAddress(rs.getString(generateColumnAlias(UserMeta.emailAddress)));
        user.setSendEmailNotifications(rs.getBoolean(generateNotifSettingColumnAlias()));

        return user;
    }
}
