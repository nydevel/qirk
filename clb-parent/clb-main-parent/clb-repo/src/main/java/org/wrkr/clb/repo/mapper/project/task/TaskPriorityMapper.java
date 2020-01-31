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
import java.util.Map;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskPriorityMeta;

public class TaskPriorityMapper extends BaseMapper<TaskPriority> {

    public TaskPriorityMapper() {
        super();
    }

    public TaskPriorityMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskPriorityMeta.id) + ", " +
                generateSelectColumnStatement(TaskPriorityMeta.nameCode);
    }

    @Override
    public TaskPriority mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskPriority priority = new TaskPriority();

        priority.setId(rs.getLong(generateColumnAlias(TaskPriorityMeta.id)));
        priority.setNameCode(TaskPriority.Priority.valueOf(rs.getString(generateColumnAlias(TaskPriorityMeta.nameCode))));

        return priority;
    }

    public TaskPriority mapRow(Map<String, Object> result) {
        TaskPriority priority = new TaskPriority();

        priority.setId((Long) result.get(generateColumnAlias(TaskPriorityMeta.id)));
        priority.setNameCode(TaskPriority.Priority.valueOf((String) result.get(generateColumnAlias(TaskPriorityMeta.nameCode))));

        return priority;
    }
}
