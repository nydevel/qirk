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
package org.wrkr.clb.repo.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.task.TaskSubscriberMeta;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.NotificationSettingsMeta;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.user.AccountUserMapper;
import org.wrkr.clb.repo.mapper.user.EmailUserMapper;
import org.wrkr.clb.repo.mapper.user.ProfileUserMapper;
import org.wrkr.clb.repo.mapper.user.ProfileUserWithNotificationSettingsMapper;
import org.wrkr.clb.repo.mapper.user.PublicProfileUserMapper;
import org.wrkr.clb.repo.mapper.user.PublicUserMapper;
import org.wrkr.clb.repo.mapper.user.UserWithNotificationSettingMapper;

@Repository
public class JDBCUserRepo extends JDBCBaseMainRepo {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(JDBCUserRepo.class);

    private static final EmailUserMapper EMAIL_USER_MAPPER = new EmailUserMapper();

    private static final String SELECT_ID_BY_EMAIL = "SELECT " +
            EMAIL_USER_MAPPER.generateSelectColumnsStatement() + " " +
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

    @Deprecated
    private static final String SELECT_ENABLED_BY_ID_FOR_ACCOUNT = SELECT_FOR_ACCOUNT_PREFIX + " " +
            "WHERE " + UserMeta.id + " = ? " + // 1
            "AND " + UserMeta.enabled + ";";

    private static final PublicProfileUserMapper PUBLIC_PROFILE_MAPPER = new PublicProfileUserMapper();

    private static final String SELECT_BY_ID_FOR_PUBLIC_PROFILE = "SELECT " +
            PUBLIC_PROFILE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    private static final ProfileUserMapper PROFILE_MAPPER = new ProfileUserMapper();

    private static final String SELECT_BY_ID_FOR_PROFILE = "SELECT " +
            PROFILE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    private static final ProfileUserWithNotificationSettingsMapper PROFILE_WITH_NOTIF_SETTINGS_MAPPER = new ProfileUserWithNotificationSettingsMapper(
            UserMeta.TABLE_NAME, NotificationSettingsMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_FOR_PROFILE_AND_FETCH_NOTIF_SETTINGS = "SELECT " +
            PROFILE_WITH_NOTIF_SETTINGS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "INNER JOIN " + NotificationSettingsMeta.TABLE_NAME + " " +
            "ON " + UserMeta.id + " = " + NotificationSettingsMeta.userId + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    private static final String SELECT_IDS = "SELECT " + UserMeta.id + " " +
            "FROM " + UserMeta.TABLE_NAME + ";";

    private static final String SELECT_EMAILS = "SELECT " + UserMeta.emailAddress + " " +
            "FROM " + UserMeta.TABLE_NAME + ";";

    private static final String SELECT_EMAILS_BY_IDS_PREFIX = "SELECT " +
            EMAIL_USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.id + " IN ("; // 1

    private static final PublicUserMapper PUBLIC_USER_MAPPER = new PublicUserMapper();

    private static final String SELECT_AND_FETCH_ORGANIZATION_MEMBERSHIP = "SELECT " +
            PUBLIC_USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "ORDER BY " + UserMeta.id + " ASC;";

    private static final UserWithNotificationSettingMapper USER_TASK_CREATED_MAPPER = new UserWithNotificationSettingMapper(
            UserMeta.TABLE_NAME, NotificationSettingsMeta.TABLE_NAME, NotificationSettingsMeta.taskCreated);
    private static final UserWithNotificationSettingMapper USER_TASK_UPDATED_MAPPER = new UserWithNotificationSettingMapper(
            UserMeta.TABLE_NAME, NotificationSettingsMeta.TABLE_NAME, NotificationSettingsMeta.taskUpdated);
    private static final UserWithNotificationSettingMapper USER_TASK_COMMENTED_MAPPER = new UserWithNotificationSettingMapper(
            UserMeta.TABLE_NAME, NotificationSettingsMeta.TABLE_NAME, NotificationSettingsMeta.taskCommented);

    private static final String SELECT_USERS_BY_TASK_ID_AND_FETCH_NOTIFICATION_SETTING_SUFFIX = "FROM "
            + TaskSubscriberMeta.TABLE_NAME + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + TaskSubscriberMeta.TABLE_NAME + "." + TaskSubscriberMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "INNER JOIN " + NotificationSettingsMeta.TABLE_NAME + " " +
            "ON " + UserMeta.TABLE_NAME + "." + UserMeta.id + " = " +
            NotificationSettingsMeta.TABLE_NAME + "." + NotificationSettingsMeta.userId + " " +
            "WHERE " + TaskSubscriberMeta.TABLE_NAME + "." + TaskSubscriberMeta.taskId + " = ?;"; // 1

    private static final String SELECT_USERS_BY_TASK_ID_AND_FETCH_TASK_CREATED = "SELECT " +
            USER_TASK_CREATED_MAPPER.generateSelectColumnsStatement() + " " +
            SELECT_USERS_BY_TASK_ID_AND_FETCH_NOTIFICATION_SETTING_SUFFIX;

    private static final String SELECT_USERS_BY_TASK_ID_AND_FETCH_TASK_UPDATED = "SELECT " +
            USER_TASK_UPDATED_MAPPER.generateSelectColumnsStatement() + " " +
            SELECT_USERS_BY_TASK_ID_AND_FETCH_NOTIFICATION_SETTING_SUFFIX;

    private static final String SELECT_USERS_BY_TASK_ID_AND_FETCH_TASK_COMMENTED = "SELECT " +
            USER_TASK_COMMENTED_MAPPER.generateSelectColumnsStatement() + " " +
            SELECT_USERS_BY_TASK_ID_AND_FETCH_NOTIFICATION_SETTING_SUFFIX;

    @SuppressWarnings("deprecation")
    private static final String UPDATE_PASSWORD_HASH_SET_ENABLED_TRUE = "UPDATE " + UserMeta.TABLE_NAME + " " +
            "SET " + UserMeta.passwordHash + " = ?, " + // 1
            UserMeta.enabled + " = true " +
            "WHERE " + UserMeta.id + " = ?;"; // 2

    private static final String UPDATE_FULL_NAME_AND_ABOUT = "UPDATE " + UserMeta.TABLE_NAME + " " +
            "SET " + UserMeta.fullName + " = ?, " + // 1
            UserMeta.about + " = ? " + // 2
            "WHERE " + UserMeta.id + " = ?;"; // 3

    private static final String UPDATE_SET_LICENSE_ACCEPTED_TO_TRUE = "UPDATE " + UserMeta.TABLE_NAME + " " +
            "SET " + UserMeta.licenseAccepted + " = true " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    public User getByEmail(String email) {
        return queryForObjectOrNull(SELECT_ID_BY_EMAIL, EMAIL_USER_MAPPER,
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

    @Deprecated
    public User getEnabledByIdForAccount(Long userId) {
        return queryForObjectOrNull(SELECT_ENABLED_BY_ID_FOR_ACCOUNT, ACCOUNT_USER_MAPPER,
                userId);
    }

    public User getByIdForPublicProfile(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_PUBLIC_PROFILE, PUBLIC_PROFILE_MAPPER,
                userId);
    }

    public User getByIdForProfile(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_PROFILE, PROFILE_MAPPER,
                userId);
    }

    public User getByIdForProfileAndFetchNotificationSettings(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_PROFILE_AND_FETCH_NOTIF_SETTINGS, PROFILE_WITH_NOTIF_SETTINGS_MAPPER,
                userId);
    }

    public List<Long> listIds() {
        return queryForList(SELECT_IDS, Long.class);
    }

    public List<String> listEmails() {
        return queryForList(SELECT_EMAILS, String.class);
    }

    public List<User> listEmailsByIds(List<Long> ids) {
        return queryForList(insertNBindValues(SELECT_EMAILS_BY_IDS_PREFIX, ids.size(), ");"), EMAIL_USER_MAPPER, ids.toArray());
    }

    public List<User> list() {
        return queryForList(SELECT_AND_FETCH_ORGANIZATION_MEMBERSHIP, PUBLIC_USER_MAPPER);
    }

    public List<User> listByTaskIdAndFetchNotificationSetting(
            Long taskId, NotificationSettings.Setting notifSetting) {
        switch (notifSetting) {
            case TASK_CREATED:
                return queryForList(SELECT_USERS_BY_TASK_ID_AND_FETCH_TASK_CREATED, USER_TASK_CREATED_MAPPER,
                        taskId);
            case TASK_UPDATED:
                return queryForList(SELECT_USERS_BY_TASK_ID_AND_FETCH_TASK_UPDATED, USER_TASK_UPDATED_MAPPER,
                        taskId);
            case TASK_COMMENTED:
                return queryForList(SELECT_USERS_BY_TASK_ID_AND_FETCH_TASK_COMMENTED, USER_TASK_COMMENTED_MAPPER,
                        taskId);
        }
        return new ArrayList<User>();
    }

    public void updatePasswordHashAndSetEnabledToTrue(User user) {
        updateSingleRow(UPDATE_PASSWORD_HASH_SET_ENABLED_TRUE,
                user.getPasswordHash(), user.getId());
    }

    public void updateFullNameAndAbout(User user) {
        updateSingleRow(UPDATE_FULL_NAME_AND_ABOUT,
                user.getFullName(), user.getAbout(), user.getId());
    }

    public void setLicenseAcceptedToTrue(User user) {
        updateSingleRow(UPDATE_SET_LICENSE_ACCEPTED_TO_TRUE, user.getId());
    }
}
