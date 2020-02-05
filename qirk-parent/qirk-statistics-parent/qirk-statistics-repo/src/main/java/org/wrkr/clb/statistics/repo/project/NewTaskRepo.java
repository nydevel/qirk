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
package org.wrkr.clb.statistics.repo.project;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.project.NewTask_;


@Repository
@Validated
public class NewTaskRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + NewTask_.TABLE_NAME + " " +
            "(" + NewTask_.projectId + ", " + // 1
            NewTask_.projectName + ", " + // 2
            NewTask_.taskId + ", " + // 3
            NewTask_.createdAt + ", " + // 4
            NewTask_.type + ", " + // 5
            NewTask_.priority + ", " + // 6
            NewTask_.status + ") " + // 7
            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    private static final String COUNT_TASK_IDS = "SELECT COUNT(" + NewTask_.taskId + ") " +
            "FROM " + NewTask_.TABLE_NAME + ";";

    private static final String COUNT_TASK_IDS_ALIAS = "count";
    private static final String CONCAT_PROJECT_NAMES_ALIAS = "project";
    private static final String COUNT_TASK_IDS_GROUP_BY_PROJECT_ID = "SELECT " +
            "COUNT(" + NewTask_.taskId + ") AS " + COUNT_TASK_IDS_ALIAS + ", " +
            "GROUP_CONCAT(DISTINCT " + NewTask_.projectName + " ORDER BY " + NewTask_.createdAt + " ASC SEPARATOR '; ') " +
            "AS " + CONCAT_PROJECT_NAMES_ALIAS + " " +
            "FROM " + NewTask_.TABLE_NAME + " " +
            "GROUP BY " + NewTask_.projectId + " " +
            "ORDER BY " + COUNT_TASK_IDS_ALIAS + " DESC;";

    public void save(@NotNull(message = "projectId in NewTaskRepo must not be null") Long projectId,
            @NotNull(message = "projectName in NewTaskRepo must not be null") String projectName,
            @NotNull(message = "taskId in NewTaskRepo must not be null") Long taskId,
            @NotNull(message = "createdAt in NewTaskRepo must not be null") Long createdAt,
            @NotNull(message = "type in NewTaskRepo must not be null") String type,
            @NotNull(message = "priority in NewTaskRepo must not be null") String priority,
            @NotNull(message = "status in NewTaskRepo must not be null") String status) {
        getJdbcTemplate().update(INSERT,
                projectId, projectName, taskId, Timestamp.from(Instant.ofEpochMilli(createdAt)), type, priority, status);
    }

    public Long countTaskIds() {
        return queryForObjectOrNull(COUNT_TASK_IDS, Long.class);
    }

    public List<Map<String, Object>> countByProject() {
        return getJdbcTemplate().queryForList(COUNT_TASK_IDS_GROUP_BY_PROJECT_ID);
    }
}
