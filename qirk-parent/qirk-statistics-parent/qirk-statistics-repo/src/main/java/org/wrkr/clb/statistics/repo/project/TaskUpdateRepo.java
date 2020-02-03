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
package org.wrkr.clb.statistics.repo.project;

import java.sql.Timestamp;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.project.TaskUpdate_;


@Repository
@Validated
public class TaskUpdateRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + TaskUpdate_.TABLE_NAME + " " +
            "(" + TaskUpdate_.projectId + ", " + // 1
            TaskUpdate_.projectName + ", " + // 2
            TaskUpdate_.taskId + ", " + // 1
            TaskUpdate_.updatedAt + ") " + // 2
            "VALUES (?, ?, ?, ?);";

    private static final String COUNT = generateSelectCountAllStatement(TaskUpdate_.TABLE_NAME);

    public void save(@NotNull(message = "projectId in NewTaskRepo must not be null") Long projectId,
            @NotNull(message = "projectName in NewTaskRepo must not be null") String projectName,
            @NotNull(message = "taskId in TaskUpdateRepo must not be null") Long taskId,
            @NotNull(message = "updatedAt in TaskUpdateRepo must not be null") Long updatedAt) {
        getJdbcTemplate().update(INSERT,
                projectId, projectName, taskId, Timestamp.from(Instant.ofEpochMilli(updatedAt)));
    }

    public Long count() {
        return queryForObjectOrNull(COUNT, Long.class);
    }
}
