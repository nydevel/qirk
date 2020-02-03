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

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.repo.mapper.organization.ShortOrganizationMemberWithUserMapper;
import org.wrkr.clb.repo.mapper.project.ShortProjectMapper;

public class TaskWithEverythingForListMapper extends BaseMapper<Task> {

    private ShortProjectMapper projectMapper;

    private ShortOrganizationMemberWithUserMapper reporterMapper;
    private ShortOrganizationMemberWithUserMapper assigneeMapper;

    private TaskTypeMapper typeMapper;
    private TaskPriorityMapper priorityMapper;
    private TaskStatusMapper statusMapper;

    public TaskWithEverythingForListMapper(String taskTableName, String projectTableName,
            String reporterMemberTableName, String reporterUserTableName,
            String assigneeMemberTableName, String assigneeUserTableName,
            String typeTableName, String priorityTableName, String statusTableName) {
        super(taskTableName);
        projectMapper = new ShortProjectMapper(projectTableName);
        reporterMapper = new ShortOrganizationMemberWithUserMapper(reporterMemberTableName, reporterUserTableName);
        assigneeMapper = new ShortOrganizationMemberWithUserMapper(assigneeMemberTableName, assigneeUserTableName);
        typeMapper = new TaskTypeMapper(typeTableName);
        priorityMapper = new TaskPriorityMapper(priorityTableName);
        statusMapper = new TaskStatusMapper(statusTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskMeta.id) + ", " +
                generateSelectColumnStatement(TaskMeta.number) + ", " +
                generateSelectColumnStatement(TaskMeta.summary) + ", " +
                generateSelectColumnStatement(TaskMeta.createdAt) + ", " +
                generateSelectColumnStatement(TaskMeta.updatedAt) + ", " +
                projectMapper.generateSelectColumnsStatement() + ", " +
                reporterMapper.generateSelectColumnsStatement() + ", " +
                assigneeMapper.generateSelectColumnsStatement() + ", " +
                typeMapper.generateSelectColumnsStatement() + ", " +
                priorityMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong(generateColumnAlias(TaskMeta.id)));
        task.setNumber(rs.getLong(generateColumnAlias(TaskMeta.number)));
        task.setSummary(rs.getString(generateColumnAlias(TaskMeta.summary)));
        task.setCreatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.createdAt)));
        task.setUpdatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.updatedAt)));

        task.setProject(projectMapper.mapRow(rs, rowNum));
        task.setReporter(reporterMapper.mapRow(rs, rowNum));
        if (rs.getObject(assigneeMapper.generateColumnAlias(OrganizationMemberMeta.id)) != null) {
            task.setAssignee(assigneeMapper.mapRow(rs, rowNum));
        }
        task.setType(typeMapper.mapRow(rs, rowNum));
        task.setPriority(priorityMapper.mapRow(rs, rowNum));
        task.setStatus(statusMapper.mapRow(rs, rowNum));

        return task;
    }
}
