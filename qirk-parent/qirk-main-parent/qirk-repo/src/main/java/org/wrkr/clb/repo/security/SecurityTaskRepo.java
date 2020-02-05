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
package org.wrkr.clb.repo.security;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.security.SecurityTaskWithProjectAndMembershipMapper;
import org.wrkr.clb.repo.mapper.security.SecurityTaskWithProjectMapper;

@Repository
public class SecurityTaskRepo extends JDBCBaseMainRepo {

    private static final SecurityTaskWithProjectMapper SECURITY_TASK_MAPPER = new SecurityTaskWithProjectMapper(
            TaskMeta.TABLE_NAME, ProjectMeta.DEFAULT);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY = "SELECT " +
            SECURITY_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NUMBER_AND_PROJECT_ID_AND_FETCH_PROJECT_FOR_SECURITY = "SELECT " +
            SECURITY_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.number + " = ? " + // 1
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NUMBER_AND_PROJECT_UI_ID_AND_FETCH_PROJECT_FOR_SECURITY = "SELECT " +
            SECURITY_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.number + " = ? " + // 1
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ?;"; // 1

    private static final SecurityTaskWithProjectAndMembershipMapper SECURITY_TASK_WITH_MEMBERSHIP_MAPPER = new SecurityTaskWithProjectAndMembershipMapper(
            TaskMeta.TABLE_NAME, ProjectMeta.DEFAULT, ProjectMemberMeta.DEFAULT, UserMeta.DEFAULT);

    private static final String SELECT_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY_PREFIX = "SELECT "
            + SECURITY_TASK_WITH_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            SecurityProjectRepo.JOIN_NOT_FIRED_PROJECT_MEMBER_AND_USER_ON_PROJECT_ID_AND_USER_ID + " ";

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY = ""
            + SELECT_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY_PREFIX +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 2

    private static final String SELECT_BY_NUMBER_AND_PROJECT_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY = ""
            + SELECT_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY_PREFIX +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.number + " = ? " + // 2
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 3

    private static final String SELECT_BY_NUMBER_AND_PROJECT_UI_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY = ""
            + SELECT_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY_PREFIX +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.number + " = ? " + // 2
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ?;"; // 3

    public Task getByIdAndFetchProjectForSecurity(Long taskId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY, SECURITY_TASK_MAPPER,
                taskId);
    }

    public Task getByNumberAndProjectIdAndFetchProjectForSecurity(Long number, Long projectId) {
        return queryForObjectOrNull(SELECT_BY_NUMBER_AND_PROJECT_ID_AND_FETCH_PROJECT_FOR_SECURITY, SECURITY_TASK_MAPPER,
                number, projectId);
    }

    public Task getByNumberAndProjectUiIdAndFetchProjectForSecurity(Long number, String projectUiId) {
        return queryForObjectOrNull(SELECT_BY_NUMBER_AND_PROJECT_UI_ID_AND_FETCH_PROJECT_FOR_SECURITY, SECURITY_TASK_MAPPER,
                number, projectUiId);
    }

    public Task getByIdAndFetchProjectAndMembershipByUserIdForSecurity(Long taskId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY,
                SECURITY_TASK_WITH_MEMBERSHIP_MAPPER,
                userId, taskId);
    }

    public Task getByNumberAndProjectIdAndFetchProjectAndMembershipByUserIdForSecurity(Long number, Long projectId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_NUMBER_AND_PROJECT_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY,
                SECURITY_TASK_WITH_MEMBERSHIP_MAPPER,
                userId, number, projectId);
    }

    public Task getByNumberAndProjectUiIdAndFetchProjectAndMembershipByUserIdForSecurity(
            Long number, String projectUiId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_NUMBER_AND_PROJECT_UI_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY,
                SECURITY_TASK_WITH_MEMBERSHIP_MAPPER,
                userId, number, projectUiId);
    }
}
