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
package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.model.project.task.Task;

public class ShortTaskWithPriorityAndStatusMapper extends ShortTaskMapper {

    private TaskPriorityMapper priorityMapper;
    private TaskStatusMapper statusMapper;

    public ShortTaskWithPriorityAndStatusMapper(String taskTableName, String priorityTableName, String statusTableName) {
        super(taskTableName);
        priorityMapper = new TaskPriorityMapper(priorityTableName);
        statusMapper = new TaskStatusMapper(statusTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                priorityMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = super.mapRow(rs, rowNum);

        task.setPriority(priorityMapper.mapRow(rs, rowNum));
        task.setStatus(statusMapper.mapRow(rs, rowNum));

        return task;
    }

    @Override
    public Task mapRow(Map<String, Object> result) {
        Task task = super.mapRow(result);

        task.setPriority(priorityMapper.mapRow(result));
        task.setStatus(statusMapper.mapRow(result));

        return task;
    }
}
