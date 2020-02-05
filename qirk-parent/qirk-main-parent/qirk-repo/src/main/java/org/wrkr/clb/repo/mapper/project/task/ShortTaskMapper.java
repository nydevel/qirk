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

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;

public class ShortTaskMapper extends BaseMapper<Task> {

    public ShortTaskMapper() {
        super();
    }

    public ShortTaskMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskMeta.id) + ", " +
                generateSelectColumnStatement(TaskMeta.recordVersion) + ", " +
                generateSelectColumnStatement(TaskMeta.number) + ", " +
                generateSelectColumnStatement(TaskMeta.summary);
    }

    @Override
    public Task mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong(generateColumnAlias(TaskMeta.id)));
        task.setRecordVersion(rs.getLong(generateColumnAlias(TaskMeta.recordVersion)));
        task.setNumber(rs.getLong(generateColumnAlias(TaskMeta.number)));
        task.setSummary(rs.getString(generateColumnAlias(TaskMeta.summary)));

        return task;
    }

    public Task mapRow(Map<String, Object> result) {
        Task task = new Task();

        task.setId((Long) result.get(generateColumnAlias(TaskMeta.id)));
        task.setRecordVersion((Long) result.get(generateColumnAlias(TaskMeta.recordVersion)));
        task.setNumber((Long) result.get(generateColumnAlias(TaskMeta.number)));
        task.setSummary((String) result.get(generateColumnAlias(TaskMeta.summary)));

        return task;
    }
}
