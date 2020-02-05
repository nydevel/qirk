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

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.project.TaskStatusUpdate_;


@Repository
@Validated
public class TaskStatusUpdateRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + TaskStatusUpdate_.TABLE_NAME + " " +
            "(" + TaskStatusUpdate_.projectId + ", " + // 1
            TaskStatusUpdate_.projectName + ", " + // 2
            TaskStatusUpdate_.taskId + ", " + // 1
            TaskStatusUpdate_.updatedAt + ", " + // 2
            TaskStatusUpdate_.status + ") " + // 3
            "VALUES (?, ?, ?, ?, ?);";

    private static final String COUNT = generateSelectCountAllStatement(TaskStatusUpdate_.TABLE_NAME);

    public void save(@NotNull(message = "projectId in NewTaskRepo must not be null") Long projectId,
            @NotNull(message = "projectName in NewTaskRepo must not be null") String projectName,
            @NotNull(message = "taskId in TaskStatusUpdateRepo must not be null") Long taskId,
            @NotNull(message = "updatedAt in TaskStatusUpdateRepo must not be null") Long updatedAt,
            @NotNull(message = "status in TaskStatusUpdateRepo must not be null") String status) {
        getJdbcTemplate().update(INSERT,
                projectId, projectName, taskId, Timestamp.from(Instant.ofEpochMilli(updatedAt)), status);
    }

    public Long count() {
        return queryForObjectOrNull(COUNT, Long.class);
    }
}
