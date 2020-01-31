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
package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskHashtagToTaskMeta;

public class TaskHashtagWithTaskCountMapper extends TaskHashtagMapper {

    private static final String TASKS_COUNT_COLUMN_NAME = "tasks_count";

    public TaskHashtagWithTaskCountMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                "COUNT(" + TaskHashtagToTaskMeta.taskId + ") " +
                "AS " + TASKS_COUNT_COLUMN_NAME;
    }

    @Override
    public TaskHashtag mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaskHashtag hashtag = super.mapRow(rs, rowNum);
        hashtag.setTasksCount(rs.getLong(TASKS_COUNT_COLUMN_NAME));
        return hashtag;
    }
}
