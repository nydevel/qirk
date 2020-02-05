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

import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.mapper.project.ProjectMemberWithUserMapper;

public class LinkedTaskMapper extends ShortTaskMapper {

    private ProjectMemberWithUserMapper assigneeMapper;
    private TaskStatusMapper statusMapper;

    public LinkedTaskMapper(String taskTableName, String statusTableName,
            ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(taskTableName);
        assigneeMapper = new ProjectMemberWithUserMapper(projectMemberMeta, userMeta);
        statusMapper = new TaskStatusMapper(statusTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                assigneeMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = super.mapRow(rs, rowNum);

        if (rs.getObject(assigneeMapper.generateColumnAlias(ProjectMemberMeta.id)) != null) {
            task.setAssignee(assigneeMapper.mapRow(rs, rowNum));
        }
        task.setStatus(statusMapper.mapRow(rs, rowNum));

        return task;
    }
}
