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
package org.wrkr.clb.repo.project.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.project.task.TaskType.Type;
import org.wrkr.clb.model.project.task.TaskTypeMeta;
import org.wrkr.clb.repo.EnumRepo;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.task.TaskTypeMapper;

@Repository
public class TaskTypeRepo extends JDBCBaseMainRepo implements EnumRepo<TaskType, TaskType.Type> {

    private static final TaskTypeMapper TYPE_MAPPER = new TaskTypeMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            TYPE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskTypeMeta.TABLE_NAME + " " +
            "WHERE " + TaskTypeMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            TYPE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskTypeMeta.TABLE_NAME + " " +
            "WHERE " + TaskTypeMeta.nameCode + " = ?;"; // 1

    private static final String SELECT = "SELECT " +
            TYPE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskTypeMeta.TABLE_NAME + ";";

    public TaskType get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, TYPE_MAPPER, statusId);
    }

    @Override
    public TaskType getByNameCode(Type status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, TYPE_MAPPER, status.toString());
    }

    public List<TaskType> list() {
        return queryForList(SELECT, TYPE_MAPPER);
    }

    public Map<String, TaskType> mapNameCodeToType() {
        Map<String, TaskType> nameCodeToType = new HashMap<String, TaskType>();
        List<TaskType> types = queryForList(SELECT, TYPE_MAPPER);
        for (TaskType type : types) {
            nameCodeToType.put(type.getNameCode().toString(), type);
        }
        return nameCodeToType;
    }
}
