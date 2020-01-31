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

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.project.task.TaskSubscriberMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class TaskSubscriberRepo extends JDBCBaseMainRepo {

    private static final String EXISTS = "SELECT 1 FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.userId + " = ? " + // 1
            "AND " + TaskSubscriberMeta.taskId + " = ?;"; // 2

    private static final String INSERT = "INSERT INTO " + TaskSubscriberMeta.TABLE_NAME + " " +
            "(" + TaskSubscriberMeta.userId + ", " + // 1
            TaskSubscriberMeta.taskId + ") " + // 2
            "VALUES (?, ?);";

    private static final String SELECT_USER_IDS_BY_TASK_ID = "SELECT " +
            TaskSubscriberMeta.userId + " " +
            "FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.taskId + " = ?;"; // 1

    private static final String COUNT_USER_IDS_BY_TASK_ID = "SELECT COUNT(" +
            TaskSubscriberMeta.userId + ") " +
            "FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.taskId + " = ?;"; // 1

    private static final String DELETE = "DELETE FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.taskId + " = ? " + // 1
            "AND " + TaskSubscriberMeta.userId + " = ?;"; // 2

    private static final String DELETE_BY_USER_ID_AND_PROJECT_ID = "DELETE FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.userId + " = ? " + // 1
            "AND " + TaskSubscriberMeta.taskId + " IN ( " +
            "SELECT " + TaskMeta.id + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskMeta.projectId + " = ?);"; // 2

    private static final String DELETE_BY_USER_ID_AND_ORGANIZATION_ID = "DELETE FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.userId + " = ? " + // 1
            "AND " + TaskSubscriberMeta.taskId + " IN ( " +
            "SELECT " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = ?);"; // 2

    private static final String DELETE_PRIVATE_BY_USER_ID_AND_ORGANIZATION_ID = "DELETE FROM " +
            TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.userId + " = ? " + // 1
            "AND " + TaskSubscriberMeta.taskId + " IN ( " +
            "SELECT " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = ? " +
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.isPrivate + ");"; // 2

    private static final String DELETE_NOT_MEMBERS_BY_PROJECT_ID = "DELETE FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.userId + " NOT IN (" +
            "SELECT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.userId + " " +
            "FROM " + ProjectMemberMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMemberMeta.projectId + " = ? " + // 1
            "AND NOT " + ProjectMemberMeta.fired + ") " +
            "AND " + TaskSubscriberMeta.taskId + " IN ( " +
            "SELECT " + TaskMeta.id + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskMeta.projectId + " = ?);"; // 2

    public boolean exists(long userId, long taskId) {
        return exists(EXISTS, userId, taskId);
    }

    public void save(long userId, long taskId) {
        getJdbcTemplate().update(INSERT, userId, taskId);
    }

    public List<Long> listUserIdsByTaskId(long taskId) {
        return queryForList(SELECT_USER_IDS_BY_TASK_ID, Long.class, taskId);
    }

    public Long countByTaskId(Long taskId) {
        return queryForObjectOrNull(COUNT_USER_IDS_BY_TASK_ID, Long.class, taskId);
    }

    public void delete(long taskId, long userId) {
        getJdbcTemplate().update(DELETE, taskId, userId);
    }

    public void deleteByUserIdAndProjectId(long userId, Long projectId) {
        getJdbcTemplate().update(DELETE_BY_USER_ID_AND_PROJECT_ID, userId, projectId);
    }

    public void deleteByUserIdAndOrganizationId(long userId, Long organizationId) {
        getJdbcTemplate().update(DELETE_BY_USER_ID_AND_ORGANIZATION_ID, userId, organizationId);
    }

    public void deletePrivateByUserIdAndOrganizationId(long userId, Long organizationId) {
        getJdbcTemplate().update(DELETE_PRIVATE_BY_USER_ID_AND_ORGANIZATION_ID, userId, organizationId);
    }

    public void deleteNonMembersByProjectId(Long projectId) {
        getJdbcTemplate().update(DELETE_NOT_MEMBERS_BY_PROJECT_ID, projectId, projectId);
    }
}
