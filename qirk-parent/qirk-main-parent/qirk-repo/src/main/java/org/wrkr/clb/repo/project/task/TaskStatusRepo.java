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
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskStatus.Status;
import org.wrkr.clb.model.project.task.TaskStatusMeta;
import org.wrkr.clb.repo.EnumRepo;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.task.TaskStatusMapper;

@Repository
public class TaskStatusRepo extends JDBCBaseMainRepo implements EnumRepo<TaskStatus, TaskStatus.Status> {

    private static final TaskStatusMapper STATUS_MAPPER = new TaskStatusMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskStatusMeta.TABLE_NAME + " " +
            "WHERE " + TaskStatusMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskStatusMeta.TABLE_NAME + " " +
            "WHERE " + TaskStatusMeta.nameCode + " = ?;"; // 1

    private static final String SELECT = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskStatusMeta.TABLE_NAME + ";";

    public TaskStatus get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, STATUS_MAPPER, statusId);
    }

    @Override
    public TaskStatus getByNameCode(Status status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, STATUS_MAPPER, status.toString());
    }

    public List<TaskStatus> list() {
        return queryForList(SELECT, STATUS_MAPPER);
    }

    public Map<String, TaskStatus> mapNameCodeToStatus() {
        Map<String, TaskStatus> nameCodeToStatus = new HashMap<String, TaskStatus>();
        List<TaskStatus> statuses = queryForList(SELECT, STATUS_MAPPER);
        for (TaskStatus status : statuses) {
            nameCodeToStatus.put(status.getNameCode().toString(), status);
        }
        return nameCodeToStatus;
    }
}
