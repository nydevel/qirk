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
