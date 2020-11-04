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
