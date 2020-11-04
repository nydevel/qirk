package org.wrkr.clb.repo.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.task.TaskSubscriberMeta;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.NotificationSettingsMeta;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.user.AccountUserMapper;
import org.wrkr.clb.repo.mapper.user.UserWithNotificationSettingsMapper;
import org.wrkr.clb.repo.mapper.user.PublicUserMapper;
import org.wrkr.clb.repo.mapper.user.PublicUserWithProjectMembershipMapper;
import org.wrkr.clb.repo.mapper.user.UserMapper;
import org.wrkr.clb.repo.mapper.user.UserWithNotificationSettingMapper;

@Repository
public class JDBCUserRepo extends JDBCBaseMainRepo {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(JDBCUserRepo.class);

    private static final PublicUserMapper PUBLIC_USER_MAPPER = new PublicUserMapper();

    private static final String SELECT_BY_ID_FOR_PUBLIC = "SELECT " +
            PUBLIC_USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    private static final UserMapper USER_MAPPER = new UserMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_EMAIL = "SELECT " +
            USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.emailAddress + " = ?;"; // 1

    private static final AccountUserMapper ACCOUNT_USER_MAPPER = new AccountUserMapper();

    private static final String SELECT_FOR_ACCOUNT_PREFIX = "SELECT " +
            ACCOUNT_USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME;

    private static final String SELECT_BY_ID_FOR_ACCOUNT = SELECT_FOR_ACCOUNT_PREFIX + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_USERNAME_FOR_ACCOUNT = SELECT_FOR_ACCOUNT_PREFIX + " " +
            "WHERE " + UserMeta.username + " = ?;"; // 1

    private static final String SELECT_BY_EMAIL_FOR_ACCOUNT = SELECT_FOR_ACCOUNT_PREFIX + " " +
            "WHERE " + UserMeta.emailAddress + " = ?;"; // 1

    private static final UserWithNotificationSettingsMapper USER_WITH_NOTIF_SETTINGS_MAPPER = new UserWithNotificationSettingsMapper(
            UserMeta.TABLE_NAME, NotificationSettingsMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_AND_FETCH_NOTIF_SETTINGS = "SELECT " +
            USER_WITH_NOTIF_SETTINGS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "INNER JOIN " + NotificationSettingsMeta.TABLE_NAME + " " +
            "ON " + UserMeta.id + " = " + NotificationSettingsMeta.userId + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    private static final String SELECT_IDS = "SELECT " + UserMeta.id + " " +
            "FROM " + UserMeta.TABLE_NAME + ";";

    private static final String SELECT_EMAILS = "SELECT " + UserMeta.emailAddress + " " +
            "FROM " + UserMeta.TABLE_NAME + ";";

    private static final String SELECT_ALL = "SELECT " +
            USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + ";";

    private static final String SELECT_BY_IDS_PREFIX = "SELECT " +
            USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.id + " IN ("; // 1

    private static final PublicUserWithProjectMembershipMapper PUBLIC_USER_WITH_PROJECT_MEMBERSHIP_MAPPER = new PublicUserWithProjectMembershipMapper(
            UserMeta.TABLE_NAME, ProjectMemberMeta.TABLE_NAME);

    private static final String SELECT_AND_FETCH_PROJECT_MEMBERSHIP = "SELECT " +
            PUBLIC_USER_WITH_PROJECT_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "LEFT JOIN " + ProjectMemberMeta.TABLE_NAME + " " +
            "ON " + UserMeta.TABLE_NAME + "." + UserMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.userId + " " +
            "AND NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + " " +
            "ORDER BY " + UserMeta.TABLE_NAME + "." + UserMeta.id + " ASC;";

    private static final UserWithNotificationSettingMapper USER_WITH_TASK_CREATED_MAPPER = new UserWithNotificationSettingMapper(
            UserMeta.TABLE_NAME, NotificationSettingsMeta.TABLE_NAME, NotificationSettingsMeta.taskCreated);
    private static final UserWithNotificationSettingMapper USER_WITH_TASK_UPDATED_MAPPER = new UserWithNotificationSettingMapper(
            UserMeta.TABLE_NAME, NotificationSettingsMeta.TABLE_NAME, NotificationSettingsMeta.taskUpdated);
    private static final UserWithNotificationSettingMapper USER_WITH_TASK_COMMENTED_MAPPER = new UserWithNotificationSettingMapper(
            UserMeta.TABLE_NAME, NotificationSettingsMeta.TABLE_NAME, NotificationSettingsMeta.taskCommented);

    private static final String SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_NOTIF_SETTING_SUFFIX = "FROM "
            + TaskSubscriberMeta.TABLE_NAME + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + TaskSubscriberMeta.TABLE_NAME + "." + TaskSubscriberMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "INNER JOIN " + NotificationSettingsMeta.TABLE_NAME + " " +
            "ON " + UserMeta.TABLE_NAME + "." + UserMeta.id + " = " +
            NotificationSettingsMeta.TABLE_NAME + "." + NotificationSettingsMeta.userId + " " +
            "WHERE " + TaskSubscriberMeta.TABLE_NAME + "." + TaskSubscriberMeta.taskId + " = ?;"; // 1

    private static final String SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_TASK_CREATED = "SELECT " +
            USER_WITH_TASK_CREATED_MAPPER.generateSelectColumnsStatement() + " " +
            SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_NOTIF_SETTING_SUFFIX;

    private static final String SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_TASK_UPDATED = "SELECT " +
            USER_WITH_TASK_UPDATED_MAPPER.generateSelectColumnsStatement() + " " +
            SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_NOTIF_SETTING_SUFFIX;

    private static final String SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_TASK_COMMENTED = "SELECT " +
            USER_WITH_TASK_COMMENTED_MAPPER.generateSelectColumnsStatement() + " " +
            SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_NOTIF_SETTING_SUFFIX;

    private static final String UPDATE_PASSWORD_HASH = "UPDATE " + UserMeta.TABLE_NAME + " " +
            "SET " + UserMeta.passwordHash + " = ? " + // 1
            "WHERE " + UserMeta.id + " = ?;"; // 2

    private static final String UPDATE_FULL_NAME = "UPDATE " + UserMeta.TABLE_NAME + " " +
            "SET " + UserMeta.fullName + " = ? " + // 1
            "WHERE " + UserMeta.id + " = ?;"; // 2

    public User getByIdForPublic(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_PUBLIC, PUBLIC_USER_MAPPER,
                userId);
    }

    public User getById(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID, USER_MAPPER,
                userId);
    }

    public User getByEmail(String email) {
        return queryForObjectOrNull(SELECT_BY_EMAIL, USER_MAPPER,
                email);
    }

    public User getByIdForAccount(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_ACCOUNT, ACCOUNT_USER_MAPPER,
                userId);
    }

    public User getByUsernameForAccount(String username) {
        return queryForObjectOrNull(SELECT_BY_USERNAME_FOR_ACCOUNT, ACCOUNT_USER_MAPPER,
                username);
    }

    public User getByEmailForAccount(String email) {
        return queryForObjectOrNull(SELECT_BY_EMAIL_FOR_ACCOUNT, ACCOUNT_USER_MAPPER,
                email);
    }

    public User getByIdAndFetchNotificationSettings(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_NOTIF_SETTINGS, USER_WITH_NOTIF_SETTINGS_MAPPER,
                userId);
    }

    public List<Long> listIds() {
        return queryForList(SELECT_IDS, Long.class);
    }

    public List<String> listEmails() {
        return queryForList(SELECT_EMAILS, String.class);
    }

    public List<User> list() {
        return queryForList(SELECT_ALL, USER_MAPPER);
    }

    public List<User> listByIds(Collection<Long> ids) {
        return queryForList(insertNBindValues(SELECT_BY_IDS_PREFIX, ids.size(), ");"), USER_MAPPER,
                ids.toArray());
    }

    public List<User> listAndFetchProjectMembership() {
        List<User> results = new ArrayList<User>();

        for (Map<String, Object> row : getJdbcTemplate()
                .queryForList(SELECT_AND_FETCH_PROJECT_MEMBERSHIP)) {
            User lastUser = (results.isEmpty() ? null : results.get(results.size() - 1));

            if (lastUser == null || !lastUser.getId().equals(
                    (Long) row.get(PUBLIC_USER_WITH_PROJECT_MEMBERSHIP_MAPPER.generateColumnAlias(UserMeta.id)))) {
                results.add(PUBLIC_USER_WITH_PROJECT_MEMBERSHIP_MAPPER.mapRow(row));

            } else {
                lastUser.getProjectMembership().add(PUBLIC_USER_WITH_PROJECT_MEMBERSHIP_MAPPER.mapRowForProjectMember(row));
            }
        }

        return results;
    }

    public List<User> listBySubscribedTaskIdAndFetchNotificationSetting(
            Long taskId, NotificationSettings.Setting notifSetting) {
        switch (notifSetting) {
            case TASK_CREATED:
                return queryForList(SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_TASK_CREATED, USER_WITH_TASK_CREATED_MAPPER,
                        taskId);
            case TASK_UPDATED:
                return queryForList(SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_TASK_UPDATED, USER_WITH_TASK_UPDATED_MAPPER,
                        taskId);
            case TASK_COMMENTED:
                return queryForList(SELECT_USERS_BY_SUBSCRIBED_TASK_ID_AND_FETCH_TASK_COMMENTED, USER_WITH_TASK_COMMENTED_MAPPER,
                        taskId);
        }
        return new ArrayList<User>();
    }

    public void updatePasswordHash(User user) {
        updateSingleRow(UPDATE_PASSWORD_HASH,
                user.getPasswordHash(), user.getId());
    }

    public void updateFullName(User user) {
        updateSingleRow(UPDATE_FULL_NAME,
                user.getFullName(), user.getId());
    }
}
