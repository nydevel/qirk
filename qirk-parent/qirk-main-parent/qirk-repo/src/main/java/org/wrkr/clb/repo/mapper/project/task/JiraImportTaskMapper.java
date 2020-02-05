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

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraTaskMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;

public class JiraImportTaskMapper extends BaseMapper<Task> {

    private TaskTypeMapper typeMapper;
    private TaskPriorityMapper priorityMapper;
    private TaskStatusMapper statusMapper;

    public JiraImportTaskMapper(String taskTableName, String typeTableName, String priorityTableName, String statusTableName) {
        super(taskTableName);
        typeMapper = new TaskTypeMapper(typeTableName);
        priorityMapper = new TaskPriorityMapper(priorityTableName);
        statusMapper = new TaskStatusMapper(statusTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskMeta.id) + ", " +
                generateSelectColumnStatement(TaskMeta.number) + ", " +
                generateSelectColumnStatement(TaskMeta.summary) + ", " +
                generateSelectColumnStatement(TaskMeta.descriptionMd) + ", " +
                generateSelectColumnStatement(TaskMeta.descriptionHtml) + ", " +
                generateSelectColumnStatement(TaskMeta.reporterId) + ", " +
                generateSelectColumnStatement(TaskMeta.assigneeId) + ", " +
                generateSelectColumnStatement(TaskMeta.createdAt) + ", " +
                generateSelectColumnStatement(TaskMeta.updatedAt) + ", " +
                typeMapper.generateSelectColumnsStatement() + ", " +
                priorityMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement() + ", " +
                ImportedJiraTaskMeta.jiraTaskId;
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong(generateColumnAlias(TaskMeta.id)));
        task.setNumber(rs.getLong(generateColumnAlias(TaskMeta.number)));
        task.setSummary(rs.getString(generateColumnAlias(TaskMeta.summary)));
        task.setDescriptionMd(rs.getString(generateColumnAlias(TaskMeta.descriptionMd)));
        task.setDescriptionHtml(rs.getString(generateColumnAlias(TaskMeta.descriptionHtml)));
        task.setReporterId(rs.getLong(generateColumnAlias(TaskMeta.reporterId)));
        task.setAssigneeId((Long) rs.getObject(generateColumnAlias(TaskMeta.assigneeId))); // nullable
        task.setCreatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.createdAt)));
        task.setUpdatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.updatedAt)));

        task.setType(typeMapper.mapRow(rs, rowNum));
        task.setPriority(priorityMapper.mapRow(rs, rowNum));
        task.setStatus(statusMapper.mapRow(rs, rowNum));

        task.setJiraTaskId(rs.getLong(ImportedJiraTaskMeta.jiraTaskId));

        return task;
    }
}
