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
package org.wrkr.clb.repo.project.task;

import java.sql.Array;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.DropboxSettingsMeta;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.organization.OrganizationMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraTaskMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskCardMeta;
import org.wrkr.clb.model.project.task.TaskHashtagMeta;
import org.wrkr.clb.model.project.task.TaskHashtagToTaskMeta;
import org.wrkr.clb.model.project.task.TaskLinkMeta;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.project.task.TaskPriorityMeta;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskStatusMeta;
import org.wrkr.clb.model.project.task.TaskTypeMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseIdRepo;
import org.wrkr.clb.repo.context.TaskSearchContext;
import org.wrkr.clb.repo.mapper.project.task.ChatTaskMapper;
import org.wrkr.clb.repo.mapper.project.task.DropboxTaskMapper;
import org.wrkr.clb.repo.mapper.project.task.JiraImportTaskMapper;
import org.wrkr.clb.repo.mapper.project.task.LinkedTaskMapper;
import org.wrkr.clb.repo.mapper.project.task.ShortTaskMapper;
import org.wrkr.clb.repo.mapper.project.task.ShortTaskWithPriorityAndStatusMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskNumberWithProjectUiIdMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskWithEverythingForDatasyncMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskWithEverythingForListMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskWithEverythingForReadMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskWithEverythingForUpdateMapper;
import org.wrkr.clb.repo.mapper.project.task.UpdateCardTaskMapper;
import org.wrkr.clb.repo.sort.SortingOption;

@Repository
public class JDBCTaskRepo extends JDBCBaseIdRepo {

    private static final String INSERT = "INSERT INTO " + TaskMeta.TABLE_NAME + " " +
            "(" + TaskMeta.recordVersion + ", " + // 1
            TaskMeta.projectId + ", " + // 2
            TaskMeta.number + ", " + // 3
            TaskMeta.descriptionMd + ", " + // 4
            TaskMeta.descriptionHtml + ", " + // 5
            TaskMeta.summary + ", " + // 6
            TaskMeta.reporterId + ", " + // 7
            TaskMeta.assigneeId + ", " + // 8
            TaskMeta.createdAt + ", " + // 9
            TaskMeta.updatedAt + ", " + // 10
            TaskMeta.typeId + ", " + // 11
            TaskMeta.priorityId + ", " + // 12
            TaskMeta.statusId + ") " + // 13
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String ALIVE_PREDICATE = "NOT " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.nameCode + " = '" + TaskStatus.Status.CLOSED.toString() + "' " +
            "AND NOT " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.nameCode + " = '" + TaskStatus.Status.REJECTED.toString() + "' ";

    private static final String SELECT_1_FOR_ALIVE_BY_CARD_ID = "SELECT 1 FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.cardId + " = ? " + // 1
            "AND " + ALIVE_PREDICATE + ";";

    private static final UpdateCardTaskMapper UPDATE_CARD_TASK_MAPPER = new UpdateCardTaskMapper(
            TaskMeta.TABLE_NAME, TaskStatusMeta.TABLE_NAME);

    private static final String SELECT_ALIVE_FOR_UPDATE_CARD_BY_ID = "SELECT " +
            UPDATE_CARD_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final String SELECT_PROJECT_ID_BY_ID = "SELECT " + TaskMeta.projectId + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskMeta.id + " = ?;"; // 1

    private static final ShortTaskMapper PARENT_TASK_MAPPER = new ShortTaskMapper();

    private static final String SELECT_PARENT_TASK_ID_BY_ID_AND_PROJECT_ID = "SELECT " +
            PARENT_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskMeta.id + " = ? " + // 1
            "AND " + TaskMeta.projectId + " = ?;"; // 2

    private static final TaskNumberWithProjectUiIdMapper SHORT_TASK_WITH_PROJECT_MAPPER = new TaskNumberWithProjectUiIdMapper(
            TaskMeta.TABLE_NAME, ProjectMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT = "SELECT " +
            SHORT_TASK_WITH_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final String PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS = "proj_dropbox_settings";
    private static final String ORG_DROPBOX_SETTINGS_TABLE_ALIAS = "org_dropbox_settings";
    @Deprecated
    private static final DropboxTaskMapper DROPBOX_TASK_MAPPER = new DropboxTaskMapper(
            TaskMeta.TABLE_NAME, ProjectMeta.TABLE_NAME, PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS,
            OrganizationMeta.TABLE_NAME, ORG_DROPBOX_SETTINGS_TABLE_ALIAS);

    @Deprecated
    private static final String SELECT_BY_ID_FOR_DROPBOX_AND_FETCH_PROJECT = "SELECT " +
            DROPBOX_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "LEFT JOIN " + DropboxSettingsMeta.TABLE_NAME + " AS " + PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.dropboxSettingsId + " = " +
            PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS + "." + DropboxSettingsMeta.id + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "LEFT JOIN " + DropboxSettingsMeta.TABLE_NAME + " AS " + ORG_DROPBOX_SETTINGS_TABLE_ALIAS + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.dropboxSettingsId + " = " +
            ORG_DROPBOX_SETTINGS_TABLE_ALIAS + "." + DropboxSettingsMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final ChatTaskMapper CHAT_TASK_MAPPER = new ChatTaskMapper(
            TaskMeta.TABLE_NAME, ProjectMeta.TABLE_NAME, OrganizationMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_FOR_CHAT_AND_FETCH_PROJECT_AND_ORGANIZATION = "SELECT " +
            CHAT_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final String REPORTER_ORG_MEMBER_TABLE_ALIAS = "reporter";
    private static final String REPORTER_USER_TABLE_ALIAS = "reporter_user";
    private static final String ASSIGNEE_ORG_MEMBER_TABLE_ALIAS = "assignee";
    private static final String ASSIGNEE_USER_TABLE_ALIAS = "assignee_user";
    private static final TaskWithEverythingForUpdateMapper UPDATE_TASK_MAPPER = new TaskWithEverythingForUpdateMapper(
            TaskMeta.TABLE_NAME,
            ProjectMeta.TABLE_NAME, OrganizationMeta.TABLE_NAME,
            REPORTER_ORG_MEMBER_TABLE_ALIAS, REPORTER_USER_TABLE_ALIAS,
            ASSIGNEE_ORG_MEMBER_TABLE_ALIAS, ASSIGNEE_USER_TABLE_ALIAS,
            TaskTypeMeta.TABLE_NAME, TaskPriorityMeta.TABLE_NAME, TaskStatusMeta.TABLE_NAME, TaskCardMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_AND_FETCH_EVERYTHING_FOR_UPDATE = "SELECT " +
            UPDATE_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + REPORTER_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.reporterId + " = " +
            REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " AS " + REPORTER_USER_TABLE_ALIAS + " " +
            "ON " + REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = " +
            REPORTER_USER_TABLE_ALIAS + "." + UserMeta.id + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.assigneeId + " = " +
            ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " " +
            "LEFT JOIN " + UserMeta.TABLE_NAME + " AS " + ASSIGNEE_USER_TABLE_ALIAS + " " +
            "ON " + ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = " +
            ASSIGNEE_USER_TABLE_ALIAS + "." + UserMeta.id + " " +
            "INNER JOIN " + TaskTypeMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.typeId + " = " +
            TaskTypeMeta.TABLE_NAME + "." + TaskTypeMeta.id + " " +
            "INNER JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "LEFT JOIN " + TaskCardMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.cardId + " = " +
            TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final TaskWithEverythingForReadMapper TASK_WITH_EVERYTHING_FOR_READ_MAPPER = new TaskWithEverythingForReadMapper(
            TaskMeta.TABLE_NAME,
            ProjectMeta.TABLE_NAME, OrganizationMeta.TABLE_NAME,
            REPORTER_ORG_MEMBER_TABLE_ALIAS, REPORTER_USER_TABLE_ALIAS,
            ASSIGNEE_ORG_MEMBER_TABLE_ALIAS, ASSIGNEE_USER_TABLE_ALIAS,
            TaskTypeMeta.TABLE_NAME, TaskPriorityMeta.TABLE_NAME, TaskStatusMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_AND_FETCH_EVERYTHING_FOR_READ = "SELECT " +
            TASK_WITH_EVERYTHING_FOR_READ_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + REPORTER_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.reporterId + " = " +
            REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " AS " + REPORTER_USER_TABLE_ALIAS + " " +
            "ON " + REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = " +
            REPORTER_USER_TABLE_ALIAS + "." + UserMeta.id + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.assigneeId + " = " +
            ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " " +
            "LEFT JOIN " + UserMeta.TABLE_NAME + " AS " + ASSIGNEE_USER_TABLE_ALIAS + " " +
            "ON " + ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = " +
            ASSIGNEE_USER_TABLE_ALIAS + "." + UserMeta.id + " " +
            "INNER JOIN " + TaskTypeMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.typeId + " = " +
            TaskTypeMeta.TABLE_NAME + "." + TaskTypeMeta.id + " " +
            "INNER JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final String SELECT_PROJECT_ID_BY_IDS = "SELECT " + TaskMeta.projectId + " "
            + "FROM " + TaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskMeta.id + " IN (?, ?);"; // 1, 2

    private static final String SELECT_ALIVE_NON_HIDDEN_TASK_IDS_BY_REPORTER_OR_ASSIGNEE_USER_ID = "SELECT " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.id + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + REPORTER_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.reporterId + " = " +
            REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.assigneeId + " = " +
            ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE (" + REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "OR" + ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = ?) " + // 2
            "AND NOT " + TaskMeta.TABLE_NAME + "." + TaskMeta.hidden + " " +
            "AND " + ALIVE_PREDICATE + ";";

    private static final LinkedTaskMapper LINKED_TASK_MAPPER = new LinkedTaskMapper(
            TaskMeta.TABLE_NAME, OrganizationMemberMeta.TABLE_NAME, UserMeta.TABLE_NAME, TaskStatusMeta.TABLE_NAME);

    private static final String SELECT_BY_PROJECT_ID_AND_IDS_AND_FETCH_ASSIGNEE_AND_STATUS = "SELECT " +
            LINKED_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.assigneeId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " " +
            "LEFT JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = ? " + // 1
            "AND " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ANY(?);"; // 2

    private static final String SELECT_BY_LINKED_TASK_ID_AND_FETCH_ASSIGNEE_AND_STATUS = "SELECT " +
            LINKED_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskLinkMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + TaskLinkMeta.TABLE_NAME + "." + TaskLinkMeta.task2Id + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.id + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.assigneeId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " " +
            "LEFT JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskLinkMeta.TABLE_NAME + "." + TaskLinkMeta.task1Id + " = ?;"; // 1

    private static final String SELECT_ALIVE_NON_HIDDEN_BY_PROJECT_ID_AND_FETCH_ASSIGNEE_AND_STATUS = "SELECT " +
            LINKED_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.assigneeId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " " +
            "LEFT JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = ? " + // 1
            "AND NOT " + TaskMeta.TABLE_NAME + "." + TaskMeta.hidden + " " +
            "AND " + ALIVE_PREDICATE + ";";

    private static final ShortTaskMapper SHORT_TASK_MAPPER = new ShortTaskMapper(TaskMeta.TABLE_NAME);

    @Deprecated
    private static final String SELECT_ALIVE_NON_HIDDEN_NON_CHILD_BY_PROJECT_ID = "SELECT " +
            SHORT_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.parentTaskId + " IS NULL " +
            "AND " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = ? " + // 1
            "AND NOT " + TaskMeta.TABLE_NAME + "." + TaskMeta.hidden + " " +
            "AND " + ALIVE_PREDICATE + ";";

    @Deprecated
    private static final String SELECT_ALIVE_NON_HIDDEN_NON_CHILD_BY_PROJECT_UI_ID = "SELECT " +
            SHORT_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.parentTaskId + " IS NULL " +
            "AND NOT " + TaskMeta.TABLE_NAME + "." + TaskMeta.hidden + " " +
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ? " + // 1
            "AND " + ALIVE_PREDICATE + ";";

    private static final String SELECT_ALIVE_NON_HIDDEN_BY_PROJECT_ID_AND_NOT_LINKED_TASK_ID_AND_FETCH_ASSIGNEE_AND_STATUS = "" +
            "SELECT " +
            LINKED_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.assigneeId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " " +
            "LEFT JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = ? " + // 1
            "AND NOT " + TaskMeta.TABLE_NAME + "." + TaskMeta.hidden + " " +
            "AND " + ALIVE_PREDICATE + " " +
            "AND NOT " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " IN " +
            "(SELECT " + TaskLinkMeta.TABLE_NAME + "." + TaskLinkMeta.task2Id + " " +
            "FROM " + TaskLinkMeta.TABLE_NAME + " " +
            "WHERE " + TaskLinkMeta.TABLE_NAME + "." + TaskLinkMeta.task1Id + " = ?);"; // 2

    private static final ShortTaskWithPriorityAndStatusMapper SHORT_TASK_WITH_PRIORITY_AND_STATUS_MAPPER = new ShortTaskWithPriorityAndStatusMapper(
            TaskMeta.TABLE_NAME, TaskPriorityMeta.TABLE_NAME, TaskStatusMeta.TABLE_NAME);

    private static final String SELECT_ALIVE_CARDLESS_BY_PROJECT_ID = "SELECT " +
            SHORT_TASK_WITH_PRIORITY_AND_STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.cardId + " IS NULL " +
            "AND " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = ? " + // 1
            "AND " + ALIVE_PREDICATE + " " +
            "ORDER BY " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " DESC";

    private static final String SELECT_ALIVE_CARDLESS_BY_PROJECT_UI_ID = "SELECT " +
            SHORT_TASK_WITH_PRIORITY_AND_STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "INNER JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.cardId + " IS NULL " +
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ? " + // 1
            "AND " + ALIVE_PREDICATE + " " +
            "ORDER BY " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " DESC";

    private static final TaskWithEverythingForListMapper TASK_WITH_EVERYTHING_FOR_LIST_MAPPER = new TaskWithEverythingForListMapper(
            TaskMeta.TABLE_NAME, ProjectMeta.TABLE_NAME,
            REPORTER_ORG_MEMBER_TABLE_ALIAS, REPORTER_USER_TABLE_ALIAS,
            ASSIGNEE_ORG_MEMBER_TABLE_ALIAS, ASSIGNEE_USER_TABLE_ALIAS,
            TaskTypeMeta.TABLE_NAME, TaskPriorityMeta.TABLE_NAME, TaskStatusMeta.TABLE_NAME);

    private static final String SELECT_BY_SEARCH_CONTEXT_PREFIX = "SELECT " +
            TASK_WITH_EVERYTHING_FOR_LIST_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + REPORTER_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.reporterId + " = " +
            REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " AS " + REPORTER_USER_TABLE_ALIAS + " " +
            "ON " + REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = " +
            REPORTER_USER_TABLE_ALIAS + "." + UserMeta.id + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.assigneeId + " = " +
            ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " " +
            "LEFT JOIN " + UserMeta.TABLE_NAME + " AS " + ASSIGNEE_USER_TABLE_ALIAS + " " +
            "ON " + ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = " +
            ASSIGNEE_USER_TABLE_ALIAS + "." + UserMeta.id + " " +
            "INNER JOIN " + TaskTypeMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.typeId + " = " +
            TaskTypeMeta.TABLE_NAME + "." + TaskTypeMeta.id + " " +
            "INNER JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " ";

    private static final String SELECT_MAX_NUMBER_BY_PROJECT_ID = "SELECT " +
            "MAX(" + TaskMeta.number + ") " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskMeta.projectId + " = ?;"; // 1

    private static final TaskWithEverythingForDatasyncMapper TASK_WITH_EVERYTHING_FOR_DATASYNC_MAPPER = new TaskWithEverythingForDatasyncMapper(
            TaskMeta.TABLE_NAME, TaskTypeMeta.TABLE_NAME, TaskPriorityMeta.TABLE_NAME, TaskStatusMeta.TABLE_NAME,
            TaskHashtagMeta.TABLE_NAME);

    private static final String SELECT_AND_FETCH_TYPE_AND_PRIORITY_AND_STATUS_AND_HASHTAGS = "SELECT " +
            TASK_WITH_EVERYTHING_FOR_DATASYNC_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskTypeMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.typeId + " = " +
            TaskTypeMeta.TABLE_NAME + "." + TaskTypeMeta.id + " " +
            "INNER JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "LEFT JOIN " + TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = " +
            TaskHashtagToTaskMeta.TABLE_NAME + "." + TaskHashtagToTaskMeta.taskId + " " +
            "LEFT JOIN " + TaskHashtagMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagToTaskMeta.TABLE_NAME + "." + TaskHashtagToTaskMeta.taskHashtagId + " = " +
            TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + ";";

    private static final JiraImportTaskMapper JIRA_IMPORT_TASK_MAPPER = new JiraImportTaskMapper(
            TaskMeta.TABLE_NAME, TaskTypeMeta.TABLE_NAME, TaskPriorityMeta.TABLE_NAME, TaskStatusMeta.TABLE_NAME);

    private static final String SELECT_BY_PROJECT_ID_AND_NOT_NULL_JIRA_TASK_ID = "SELECT " +
            JIRA_IMPORT_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ImportedJiraTaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + ImportedJiraTaskMeta.TABLE_NAME + "." + ImportedJiraTaskMeta.taskId + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.id + " " +
            "INNER JOIN " + TaskTypeMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.typeId + " = " +
            TaskTypeMeta.TABLE_NAME + "." + TaskTypeMeta.id + " " +
            "INNER JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "INNER JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = ?;"; // 1

    private static final String UPDATE = "UPDATE " + TaskMeta.TABLE_NAME + " " +
            "SET " + TaskMeta.recordVersion + " = ?, " + // 1
            TaskMeta.descriptionMd + " = ?, " + // 2
            TaskMeta.descriptionHtml + " = ?, " + // 3
            TaskMeta.summary + " = ?, " + // 4
            TaskMeta.assigneeId + " = ?, " + // 5
            TaskMeta.updatedAt + " = ?, " + // 6
            TaskMeta.typeId + " = ?, " + // 7
            TaskMeta.priorityId + " = ?, " + // 8
            TaskMeta.statusId + " = ? " + // 9
            "WHERE " + TaskMeta.id + " = ?;"; // 10

    private static final String UPDATE_AND_INC_RECORD_VERSION = "UPDATE " + TaskMeta.TABLE_NAME + " " +
            "SET " + TaskMeta.recordVersion + " = " + TaskMeta.recordVersion + " + 1, " + // 1
            TaskMeta.descriptionMd + " = ?, " + // 1
            TaskMeta.descriptionHtml + " = ?, " + // 2
            TaskMeta.summary + " = ?, " + // 3
            TaskMeta.assigneeId + " = ?, " + // 4
            TaskMeta.updatedAt + " = ?, " + // 5
            TaskMeta.typeId + " = ?, " + // 6
            TaskMeta.priorityId + " = ?, " + // 7
            TaskMeta.statusId + " = ? " + // 8
            "WHERE " + TaskMeta.id + " = ?;"; // 9

    private static final String UPDATE_RECORD_VERSION_AND_CARD_ID_AND_HIDDEN_BY_ID = "UPDATE " + TaskMeta.TABLE_NAME + " " +
            "SET " + TaskMeta.recordVersion + " = ?, " + // 1
            TaskMeta.cardId + " = ?, " + // 2
            TaskMeta.hidden + " = ? " + // 3
            "WHERE " + TaskMeta.id + " = ?;"; // 4

    private static final String UPDATE_HIDDEN_BY_CARD_ID = "UPDATE " + TaskMeta.TABLE_NAME + " " +
            "SET " + TaskMeta.hidden + " = ? " + // 1
            "WHERE " + TaskMeta.cardId + " = ?;"; // 2

    private static final String UPDATE_CARD_ID_NULL_AND_HIDDEN_FALSE_BY_CARD_ID = "UPDATE " + TaskMeta.TABLE_NAME + " " +
            "SET " + TaskMeta.cardId + " = NULL, " +
            TaskMeta.hidden + " = false " + // 1
            "WHERE " + TaskMeta.cardId + " = ?;"; // 1

    private static final String UPDATE_CARD_ID_NULL_AND_HIDDEN_FALSE_BY_ROAD_ID = "UPDATE " + TaskMeta.TABLE_NAME + " " +
            "SET " + TaskMeta.cardId + " = NULL, " +
            TaskMeta.hidden + " = false " + // 1
            "WHERE " + TaskMeta.cardId + " IN " +
            "(SELECT " + TaskCardMeta.id + " " +
            "FROM " + TaskCardMeta.TABLE_NAME + " " +
            "WHERE " + TaskCardMeta.roadId + " = ?);"; // 1

    public Task save(Task task) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, new String[] { TaskMeta.id });

            ps.setLong(1, task.getRecordVersion());
            ps.setLong(2, task.getProject().getId());
            ps.setLong(3, task.getNumber());
            ps.setString(4, task.getDescriptionMd());
            ps.setString(5, task.getDescriptionHtml());
            ps.setString(6, task.getSummary());
            ps.setLong(7, task.getReporter().getId());
            ps.setObject(8, task.getAssigneeId(), Types.BIGINT); // nullable
            ps.setTimestamp(9, Timestamp.from(task.getCreatedAt().toInstant()));
            ps.setTimestamp(10, Timestamp.from(task.getUpdatedAt().toInstant()));
            ps.setLong(11, task.getType().getId());
            ps.setLong(12, task.getPriority().getId());
            ps.setLong(13, task.getStatus().getId());

            return ps;
        }, keyHolder);

        return setIdAfterSave(task, keyHolder);
    }

    public boolean existsAliveByCardId(Long cardId) {
        return exists(SELECT_1_FOR_ALIVE_BY_CARD_ID, cardId);
    }

    public Task getForUpdateCardIdById(Long taskId) {
        return queryForObjectOrNull(SELECT_ALIVE_FOR_UPDATE_CARD_BY_ID, UPDATE_CARD_TASK_MAPPER,
                taskId);
    }

    public Long getProjectIdById(Long taskid) {
        return queryForObjectOrNull(SELECT_PROJECT_ID_BY_ID, Long.class, taskid);
    }

    public Task getParentTaskByIdAndProjectId(Long taskId, Long projectId) {
        return queryForObjectOrNull(SELECT_PARENT_TASK_ID_BY_ID_AND_PROJECT_ID, PARENT_TASK_MAPPER, taskId, projectId);
    }

    public Task getByIdAndFetchProject(Long taskId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT, SHORT_TASK_WITH_PROJECT_MAPPER, taskId);
    }

    @Deprecated
    public Task getByIdForDropboxAndFetchProject(Long taskId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_DROPBOX_AND_FETCH_PROJECT, DROPBOX_TASK_MAPPER, taskId);
    }

    public Task getByIdForChatAndFetchProjectAndOrganization(Long taskId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_CHAT_AND_FETCH_PROJECT_AND_ORGANIZATION, CHAT_TASK_MAPPER, taskId);
    }

    public Task getByIdAndFetchEverythingForUpdate(Long taskId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_EVERYTHING_FOR_UPDATE, UPDATE_TASK_MAPPER,
                taskId);
    }

    public Task getByIdAndFetchEverythingForRead(Long taskId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_EVERYTHING_FOR_READ, TASK_WITH_EVERYTHING_FOR_READ_MAPPER, taskId);
    }

    public List<Long> listProjectIdsByIds(Long task1Id, Long task2Id) {
        List<Long> results = queryForList(SELECT_PROJECT_ID_BY_IDS, Long.class, task1Id, task2Id);
        if (results.size() > 2) {
            throw new IncorrectResultSizeDataAccessException(2, results.size());
        }
        return results;
    }

    public List<Long> listAliveNonHiddenTaskIdsByNotFiredReporterOrAssigneeUserId(Long userId) {
        return queryForList(SELECT_ALIVE_NON_HIDDEN_TASK_IDS_BY_REPORTER_OR_ASSIGNEE_USER_ID, Long.class,
                userId, userId);
    }

    public List<Task> listByProjectIdAndIds(Long projectId, Set<Long> taskIds) {
        Array taskIdArray = createArrayOf(JDBCType.BIGINT.getName(), taskIds.toArray());
        return queryForList(SELECT_BY_PROJECT_ID_AND_IDS_AND_FETCH_ASSIGNEE_AND_STATUS, LINKED_TASK_MAPPER,
                projectId, taskIdArray);
    }

    public List<Task> listByLinkedTaskId(Long linkedTaskId) {
        return queryForList(SELECT_BY_LINKED_TASK_ID_AND_FETCH_ASSIGNEE_AND_STATUS, LINKED_TASK_MAPPER, linkedTaskId);
    }

    public List<Task> listAliveNonHiddenByProjectId(Long projectId) {
        return queryForList(SELECT_ALIVE_NON_HIDDEN_BY_PROJECT_ID_AND_FETCH_ASSIGNEE_AND_STATUS,
                LINKED_TASK_MAPPER,
                projectId);
    }

    @Deprecated
    public List<Task> listAliveNonHiddenNonChildByProjectId(Long projectId) {
        return queryForList(SELECT_ALIVE_NON_HIDDEN_NON_CHILD_BY_PROJECT_ID, SHORT_TASK_MAPPER, projectId);
    }

    @Deprecated
    public List<Task> listAliveNonHiddenNonChildByProjectUiId(String projectUiId) {
        return queryForList(SELECT_ALIVE_NON_HIDDEN_NON_CHILD_BY_PROJECT_UI_ID, SHORT_TASK_MAPPER, projectUiId);
    }

    public List<Task> listAliveNonHiddenByProjectIdAndNotLinkedTaskId(Long projectId, Long notLinkedTaskId) {
        return queryForList(SELECT_ALIVE_NON_HIDDEN_BY_PROJECT_ID_AND_NOT_LINKED_TASK_ID_AND_FETCH_ASSIGNEE_AND_STATUS,
                LINKED_TASK_MAPPER,
                projectId, notLinkedTaskId);
    }

    public List<Task> listAliveCardlessByProjectId(Long projectId) {
        return queryForList(SELECT_ALIVE_CARDLESS_BY_PROJECT_ID, SHORT_TASK_WITH_PRIORITY_AND_STATUS_MAPPER, projectId);
    }

    public List<Task> listAliveCardlessByProjectUiId(String projectUiId) {
        return queryForList(SELECT_ALIVE_CARDLESS_BY_PROJECT_UI_ID, SHORT_TASK_WITH_PRIORITY_AND_STATUS_MAPPER, projectUiId);
    }

    private String getOrderColumnLabel(SortingOption.ForTask sortBy) {
        switch (sortBy) {
            case ASSIGNEE:
                return ASSIGNEE_USER_TABLE_ALIAS + "." + UserMeta.username;
            case CREATED_AT:
                return TaskMeta.TABLE_NAME + "." + TaskMeta.id; // id has the same order
            case NUMBER:
                return TaskMeta.TABLE_NAME + "." + TaskMeta.id; // id has the same order
            case PRIORITY:
                return TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.importance;
            case REPORTER:
                return REPORTER_USER_TABLE_ALIAS + "." + UserMeta.username;
            case SUMMARY:
                return TaskMeta.TABLE_NAME + "." + TaskMeta.summary;
            case UPDATED_AT:
                return TaskMeta.TABLE_NAME + "." + TaskMeta.updatedAt;
        }
        return TaskMeta.TABLE_NAME + "." + TaskMeta.updatedAt;
    }

    @Deprecated
    public List<Task> search(TaskSearchContext searchContext) {
        String sql = SELECT_BY_SEARCH_CONTEXT_PREFIX;

        List<String> predicates = new ArrayList<String>(Arrays.asList(
                "NOT " + TaskMeta.TABLE_NAME + "." + TaskMeta.hidden + " "));
        List<Object> args = new ArrayList<Object>();
        if (searchContext.projectDTO.getId() != null) {
            predicates.add(TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = ? ");
            args.add(searchContext.projectDTO.getId());
        }
        if (searchContext.projectDTO.getUiId() != null) {
            predicates.add(ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ? ");
            args.add(searchContext.projectDTO.getUiId());
        }

        if (searchContext.reporterId != null) {
            predicates.add(REPORTER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " = ? ");
            args.add(searchContext.reporterId);
        }
        if (searchContext.assigneeId != null) {
            if (TaskSearchContext.UNASSIGNED_ID.equals(searchContext.assigneeId)) {
                predicates.add(ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " IS NULL ");
            } else {
                predicates.add(ASSIGNEE_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " = ? ");
                args.add(searchContext.assigneeId);
            }
        }

        if (searchContext.types.size() > 0) {
            predicates.add(insertNBindValues(
                    TaskTypeMeta.TABLE_NAME + "." + TaskTypeMeta.nameCode + " IN (", searchContext.types.size(), ") "));
            args.addAll(searchContext.types);
        }
        if (searchContext.priorities.size() > 0) {
            predicates.add(insertNBindValues(
                    TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.nameCode + " IN (", searchContext.priorities.size(),
                    ") "));
            args.addAll(searchContext.priorities);
        }
        if (searchContext.statuses.size() > 0) {
            predicates.add(insertNBindValues(
                    TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.nameCode + " IN (", searchContext.statuses.size(), ") "));
            args.addAll(searchContext.statuses);
        }

        if (!searchContext.hashtag.isBlank()) {
            sql = sql + "INNER JOIN " + TaskHashtagToTaskMeta.TABLE_NAME + " " +
                    "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = " +
                    TaskHashtagToTaskMeta.TABLE_NAME + "." + TaskHashtagToTaskMeta.taskId + " " +
                    "INNER JOIN " + TaskHashtagMeta.TABLE_NAME + " " +
                    "ON " + TaskHashtagToTaskMeta.TABLE_NAME + "." + TaskHashtagToTaskMeta.taskHashtagId + " = " +
                    TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + " ";
            predicates.add(TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.name + " = ? ");
            args.add(searchContext.hashtag);
        }

        List<String> orderByClauses = new ArrayList<String>();
        orderByClauses.add(getOrderColumnLabel(searchContext.sortBy) + " " + searchContext.sortOrder);
        if (!searchContext.sortBy.equals(SortingOption.ForTask.CREATED_AT)
                && !searchContext.sortBy.equals(SortingOption.ForTask.UPDATED_AT)
                && !searchContext.sortBy.equals(SortingOption.ForTask.NUMBER)) {
            orderByClauses.add(TaskMeta.TABLE_NAME + "." + TaskMeta.updatedAt + " DESC");
        }

        sql = sql + "WHERE " + String.join("AND ", predicates) + " " +
                "ORDER BY " + String.join(", ", orderByClauses) + ";";
        return queryForList(sql, args.toArray(), TASK_WITH_EVERYTHING_FOR_LIST_MAPPER);
    }

    public List<Task> listAndFetchTypeAndPriorityAndStatusAndHashtags() {
        List<Task> results = new ArrayList<Task>();

        for (Map<String, Object> row : getJdbcTemplate()
                .queryForList(SELECT_AND_FETCH_TYPE_AND_PRIORITY_AND_STATUS_AND_HASHTAGS)) {
            Task lastTask = (results.isEmpty() ? null : results.get(results.size() - 1));

            if (lastTask == null || !lastTask.getId().equals(
                    (Long) row.get(TASK_WITH_EVERYTHING_FOR_DATASYNC_MAPPER.generateColumnAlias(TaskMeta.id)))) {
                results.add(TASK_WITH_EVERYTHING_FOR_DATASYNC_MAPPER.mapRow(row));

            } else {
                lastTask.getHashtags().add(TASK_WITH_EVERYTHING_FOR_DATASYNC_MAPPER.mapRowForHashtag(row));
            }
        }

        return results;
    }

    public long getMaxNumberByProjectIdOrZero(Long projectId) {
        Long result = queryForObjectOrNull(SELECT_MAX_NUMBER_BY_PROJECT_ID, Long.class, projectId);
        return (result == null ? 0L : result);
    }

    public Map<Long, Task> mapJiraTaskIdToTaskByprojectId(Long projectId) {
        List<Task> results = queryForList(SELECT_BY_PROJECT_ID_AND_NOT_NULL_JIRA_TASK_ID,
                JIRA_IMPORT_TASK_MAPPER,
                projectId);
        Map<Long, Task> jiraTaskIdToTask = new HashMap<Long, Task>();
        for (Task task : results) {
            task.setProjectId(projectId);
            jiraTaskIdToTask.put(task.getJiraTaskId(), task);
        }
        return jiraTaskIdToTask;
    }

    public void update(Task task) {
        updateSingleRow(UPDATE,
                task.getRecordVersion(), task.getDescriptionMd(), task.getDescriptionHtml(), task.getSummary(),
                task.getAssigneeId(), Timestamp.from(task.getUpdatedAt().toInstant()),
                task.getType().getId(), task.getPriority().getId(), task.getStatus().getId(), task.getId());
    }

    public void updateFromJira(Task task) {
        updateSingleRow(UPDATE_AND_INC_RECORD_VERSION,
                task.getDescriptionMd(), task.getDescriptionHtml(), task.getSummary(),
                task.getAssigneeId(), Timestamp.from(task.getUpdatedAt().toInstant()),
                task.getTypeId(), task.getPriorityId(), task.getStatusId(), task.getId());
    }

    public void updateRecordVersionAndCardIdAndHiddenById(Task task) {
        updateSingleRow(UPDATE_RECORD_VERSION_AND_CARD_ID_AND_HIDDEN_BY_ID,
                task.getRecordVersion(), task.getCardId(), task.isHidden(), task.getId());
    }

    public void updateHiddenByCardId(boolean hidden, Long cardId) {
        getJdbcTemplate().update(UPDATE_HIDDEN_BY_CARD_ID,
                hidden, cardId);
    }

    public void setCardIdToNullAndHiddenToFalseByCardId(Long cardId) {
        getJdbcTemplate().update(UPDATE_CARD_ID_NULL_AND_HIDDEN_FALSE_BY_CARD_ID,
                cardId);
    }

    public void setCardIdToNullAndHiddenToFalseByRoadId(Long roadId) {
        getJdbcTemplate().update(UPDATE_CARD_ID_NULL_AND_HIDDEN_FALSE_BY_ROAD_ID,
                roadId);
    }
}
