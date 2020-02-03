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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.common.util.collections.ExtendedArrayUtils;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskHashtagMeta;
import org.wrkr.clb.model.project.task.TaskHashtagToTaskMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.task.TaskHashtagMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskHashtagWithTaskCountMapper;
import org.wrkr.clb.repo.mapper.security.SecurityTaskHashtagWithProjectAndMembershipMapper;
import org.wrkr.clb.repo.mapper.security.SecurityTaskHashtagWithProjectMapper;

@Repository
public class TaskHashtagRepo extends JDBCBaseMainRepo {

    private static final String SELECT_1_BY_TASK_HASHTAG_ID = "SELECT 1 FROM " + TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskHashtagToTaskMeta.taskHashtagId + " = ? " + // 1
            "LIMIT 1;";

    private static final SecurityTaskHashtagWithProjectMapper SECURITY_TASK_HASHTAG_MAPPER = new SecurityTaskHashtagWithProjectMapper(
            TaskHashtagMeta.TABLE_NAME, ProjectMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY = "SELECT " +
            SECURITY_TASK_HASHTAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + " = ?;"; // 1

    private static final SecurityTaskHashtagWithProjectAndMembershipMapper SECURITY_TASK_HASHTAG_WITH_MEMBERSHIP_MAPPER = new SecurityTaskHashtagWithProjectAndMembershipMapper(
            TaskHashtagMeta.TABLE_NAME, ProjectMeta.TABLE_NAME, OrganizationMemberMeta.TABLE_NAME, ProjectMemberMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY = "SELECT "
            + SECURITY_TASK_HASHTAG_WITH_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " " +
            "AND " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + " " +
            "LEFT JOIN " + ProjectMemberMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.organizationMemberId + " " +
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " " +
            "AND NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + " " +
            "WHERE " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + " = ?;"; // 2

    private static final String SELECT_IDS_BY_TASK_ID = "SELECT " +
            TaskHashtagToTaskMeta.taskHashtagId + " " +
            "FROM " + TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskHashtagToTaskMeta.taskId + " = ?;"; // 1

    private static final TaskHashtagMapper TASK_HASHTAG_MAPPER = new TaskHashtagMapper();

    private static final String SELECT_BY_TASK_ID = "SELECT " +
            TASK_HASHTAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.id + " = " + TaskHashtagToTaskMeta.taskHashtagId + " " +
            "WHERE " + TaskHashtagToTaskMeta.taskId + " = ?;"; // 1

    private static final String SELECT_BY_PROJECT_ID_AND_NAME = "SELECT " +
            TASK_HASHTAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "WHERE " + TaskHashtagMeta.projectId + " = ? " + // 1
            "AND " + TaskHashtagMeta.name + " = ?;"; // 2

    private static final String SELECT_BY_IDS_AND_PROJECT_ID_PREFIX = "SELECT " +
            TASK_HASHTAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "WHERE " + TaskHashtagMeta.projectId + " = ? " + // 1
            "AND " + TaskHashtagMeta.id + " IN ("; // 2

    private static final String SELECT_BY_PROJECT_ID_AND_NAME_LIKE = "SELECT " +
            TASK_HASHTAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "WHERE " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.projectId + " = ? " + // 1
            "AND " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.name + " LIKE ?;"; // 2

    private static final TaskHashtagMapper TABLENAME_TASK_HASHTAG_MAPPER = new TaskHashtagMapper(TaskHashtagMeta.TABLE_NAME);

    private static final String SELECT_BY_PROJECT_UI_ID_AND_NAME_LIKE = "SELECT " +
            TABLENAME_TASK_HASHTAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ? " + // 1
            "AND " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.name + " LIKE ?;";// 2

    private static final TaskHashtagWithTaskCountMapper TASK_HASHTAG_WITH_TASK_COUNT_MAPPER = new TaskHashtagWithTaskCountMapper(
            TaskHashtagMeta.TABLE_NAME);

    private static final String SELECT_BY_PROJECT_ID_AND_NAME_LIKE_AND_FETCH_TASKS_COUNT = "SELECT " +
            TASK_HASHTAG_WITH_TASK_COUNT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "LEFT JOIN " + TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + " = " +
            TaskHashtagToTaskMeta.TABLE_NAME + "." + TaskHashtagToTaskMeta.taskHashtagId + " " +
            "WHERE " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.projectId + " = ? " + // 1
            "AND " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.name + " LIKE ? " + // 2
            "GROUP BY " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + ";";

    private static final String SELECT_BY_PROJECT_UI_ID_AND_NAME_LIKE_AND_FETCH_TASKS_COUNT = "SELECT " +
            TASK_HASHTAG_WITH_TASK_COUNT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "LEFT JOIN " + TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + " = " +
            TaskHashtagToTaskMeta.TABLE_NAME + "." + TaskHashtagToTaskMeta.taskHashtagId + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ? " + // 1
            "AND " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.name + " LIKE ? " + // 2
            "GROUP BY " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + ";";

    private static final String DELETE_FROM_TASK_HASHTAG_TASK_BY_TASK_ID_PREFIX = "DELETE FROM " +
            TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskHashtagToTaskMeta.taskId + " = ?"; // 1

    private static final String DELETE_FROM_TASK_HASHTAG_TASK_BY_TASK_ID = DELETE_FROM_TASK_HASHTAG_TASK_BY_TASK_ID_PREFIX + ";";

    private static final String DELETE_FROM_TASK_HASHTAG_TASK_BY_TASK_ID_AND_IDS_TO_RETAIN = "" +
            DELETE_FROM_TASK_HASHTAG_TASK_BY_TASK_ID_PREFIX + " " +
            "AND NOT " + TaskHashtagToTaskMeta.taskHashtagId + " = ANY(?);"; // 2

    private static final String INSERT_BATCH = "INSERT INTO " +
            TaskHashtagMeta.TABLE_NAME + " " +
            "(" + TaskHashtagMeta.projectId + ", " + // 1
            TaskHashtagMeta.name + ") " + // 2
            "VALUES (unnest(?), unnest(?)) " +
            "RETURNING " + TaskHashtagMeta.id + ", " +
            TaskHashtagMeta.name + ";";

    private static final String INSERT_INTO_TASK_HASHTAG_TASK = "INSERT INTO " +
            TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "(" + TaskHashtagToTaskMeta.taskId + ", " + // 1
            TaskHashtagToTaskMeta.taskHashtagId + ") " + // 2
            "VALUES (?, ?);";

    private static final String INSERT_BATCH_INTO_TASK_HASHTAG_TASK = "INSERT INTO " +
            TaskHashtagToTaskMeta.TABLE_NAME + " " +
            "(" + TaskHashtagToTaskMeta.taskId + ", " + // 1
            TaskHashtagToTaskMeta.taskHashtagId + ") " + // 2
            "VALUES (unnest(?), unnest(?));";

    private static final String DELETE_BY_ID = "DELETE FROM " +
            TaskHashtagMeta.TABLE_NAME + " " +
            "WHERE " + TaskHashtagMeta.id + " = ?;";

    public boolean existsTaskIdByTaskHashtagId(Long hashtagId) {
        return exists(SELECT_1_BY_TASK_HASHTAG_ID, hashtagId);
    }

    public TaskHashtag getByIdAndFetchProjectForSecurity(Long hashtagId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY, SECURITY_TASK_HASHTAG_MAPPER,
                hashtagId);
    }

    public TaskHashtag getByIdAndFetchProjectAndMembershipByUserIdForSecurity(Long hashtagId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY,
                SECURITY_TASK_HASHTAG_WITH_MEMBERSHIP_MAPPER,
                userId, hashtagId);
    }

    public TaskHashtag getByProjectIdAndName(Long projectId, String hashtagName) {
        return queryForObjectOrNull(SELECT_BY_PROJECT_ID_AND_NAME, TASK_HASHTAG_MAPPER,
                projectId, hashtagName);
    }

    public List<TaskHashtag> listByIdsAndProjectId(Collection<Long> hashtagIds, Long projectId) {
        return queryForList(
                insertNBindValues(SELECT_BY_IDS_AND_PROJECT_ID_PREFIX, hashtagIds.size(), ");"),
                ArrayUtils.addAll(new Long[] { projectId }, hashtagIds.toArray(new Long[] {})),
                TASK_HASHTAG_MAPPER);
    }

    public List<Long> listIdsByTaskId(Long taskId) {
        return queryForList(SELECT_IDS_BY_TASK_ID, Long.class, taskId);
    }

    public List<TaskHashtag> listByTaskId(Long taskId) {
        return queryForList(SELECT_BY_TASK_ID, TASK_HASHTAG_MAPPER, taskId);
    }

    public List<TaskHashtag> listByProjectIdAndNamePrefix(String prefix, Long projectId) {
        return queryForList(SELECT_BY_PROJECT_ID_AND_NAME_LIKE,
                TASK_HASHTAG_MAPPER,
                projectId, prefix + "%");
    }

    public List<TaskHashtag> listByProjectUiIdAndNamePrefix(String prefix, String projectUiId) {
        return queryForList(SELECT_BY_PROJECT_UI_ID_AND_NAME_LIKE,
                TABLENAME_TASK_HASHTAG_MAPPER,
                projectUiId, prefix + "%");
    }

    public List<TaskHashtag> listByProjectIdAndNamePrefixAndFetchTasksCount(String prefix, Long projectId) {
        return queryForList(SELECT_BY_PROJECT_ID_AND_NAME_LIKE_AND_FETCH_TASKS_COUNT,
                TASK_HASHTAG_WITH_TASK_COUNT_MAPPER,
                projectId, prefix + "%");
    }

    public List<TaskHashtag> listByProjectUiIdAndNamePrefixAndFetchTasksCount(String prefix, String projectUiId) {
        return queryForList(SELECT_BY_PROJECT_UI_ID_AND_NAME_LIKE_AND_FETCH_TASKS_COUNT,
                TASK_HASHTAG_WITH_TASK_COUNT_MAPPER,
                projectUiId, prefix + "%");
    }

    public List<TaskHashtag> saveBatchByProjectIdAndNames(Long projectId, List<String> hashtagNames) {
        Array projectIdArray = createArrayOf(JDBCType.BIGINT.getName(),
                ExtendedArrayUtils.buildArrayOfElement(projectId, hashtagNames.size()));
        Array hashtagNamesArray = createArrayOf(JDBCType.VARCHAR.getName(), hashtagNames.toArray(new String[] {}));

        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_BATCH,
                    new String[] { TaskHashtagMeta.id, TaskHashtagMeta.name });
            ps.setArray(1, projectIdArray);
            ps.setArray(2, hashtagNamesArray);
            return ps;
        }, keyHolder);

        List<TaskHashtag> hashtags = new ArrayList<TaskHashtag>();
        for (Map<String, Object> map : keyHolder.getKeyList()) {
            hashtags.add(TaskHashtag.fromMap(map, projectId));
        }
        return hashtags;
    }

    public void setHashtagToTask(Long taskId, Long hashtagId) {
        getJdbcTemplate().update(INSERT_INTO_TASK_HASHTAG_TASK,
                taskId, hashtagId);
    }

    private Long[] buildHashtagIdsArrayForDeleteFromTaskHashtagTask(List<TaskHashtag> hashtagsToSet) {
        Long[] result = new Long[hashtagsToSet.size()];
        for (int i = 0; i < hashtagsToSet.size(); i++) {
            result[i] = hashtagsToSet.get(i).getId();
        }
        return result;
    }

    public void setHashtagsToTask(Long taskId, List<TaskHashtag> hashtags) {
        if (!hashtags.isEmpty()) {
            Long[] hashtagIds = buildHashtagIdsArrayForDeleteFromTaskHashtagTask(hashtags);
            Array hashtagIdArray = createArrayOf(JDBCType.BIGINT.getName(), hashtagIds);
            getJdbcTemplate().update(DELETE_FROM_TASK_HASHTAG_TASK_BY_TASK_ID_AND_IDS_TO_RETAIN,
                    taskId, hashtagIdArray);

            Set<Long> hashtagIdsToInsert = new HashSet<Long>(hashtagIds.length);
            Collections.addAll(hashtagIdsToInsert, hashtagIds);
            hashtagIdsToInsert.removeAll(listIdsByTaskId(taskId));

            Array taskIdArray = createArrayOf(JDBCType.BIGINT.getName(),
                    ExtendedArrayUtils.buildArrayOfElement(taskId, hashtagIdsToInsert.size()));
            Array hashtagIdArrayToInsert = createArrayOf(JDBCType.BIGINT.getName(), hashtagIdsToInsert.toArray(new Long[] {}));

            getJdbcTemplate().update(INSERT_BATCH_INTO_TASK_HASHTAG_TASK,
                    taskIdArray, hashtagIdArrayToInsert);
        } else {
            getJdbcTemplate().update(DELETE_FROM_TASK_HASHTAG_TASK_BY_TASK_ID, taskId);
        }
    }

    public void deleteById(Long hashtagId) {
        updateSingleRow(DELETE_BY_ID, hashtagId);
    }
}
