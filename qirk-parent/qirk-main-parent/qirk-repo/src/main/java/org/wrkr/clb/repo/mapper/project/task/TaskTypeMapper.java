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
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.project.task.TaskTypeMeta;

public class TaskTypeMapper extends BaseMapper<TaskType> {

    public TaskTypeMapper() {
        super();
    }

    public TaskTypeMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskTypeMeta.id) + ", " +
                generateSelectColumnStatement(TaskTypeMeta.nameCode);
    }

    @Override
    public TaskType mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskType type = new TaskType();

        type.setId(rs.getLong(generateColumnAlias(TaskTypeMeta.id)));
        type.setNameCode(TaskType.Type.valueOf(rs.getString(generateColumnAlias(TaskTypeMeta.nameCode))));

        return type;
    }

    public TaskType mapRow(Map<String, Object> result) {
        TaskType type = new TaskType();

        type.setId((Long) result.get(generateColumnAlias(TaskTypeMeta.id)));
        type.setNameCode(TaskType.Type.valueOf((String) result.get(generateColumnAlias(TaskTypeMeta.nameCode))));

        return type;
    }
}
