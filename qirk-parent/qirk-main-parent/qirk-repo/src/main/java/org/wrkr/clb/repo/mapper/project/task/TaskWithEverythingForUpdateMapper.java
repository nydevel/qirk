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
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.roadmap.TaskCardMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.mapper.project.ProjectMemberWithUserMapper;
import org.wrkr.clb.repo.mapper.project.ShortProjectMapper;
import org.wrkr.clb.repo.mapper.project.roadmap.TaskCardStatusMapper;

public class TaskWithEverythingForUpdateMapper extends BaseMapper<Task> {

    private ShortProjectMapper projectMapper;

    private ProjectMemberWithUserMapper reporterMapper;
    private ProjectMemberWithUserMapper assigneeMapper;

    private TaskTypeMapper typeMapper;
    private TaskPriorityMapper priorityMapper;
    private TaskStatusMapper statusMapper;

    private TaskCardStatusMapper cardMapper;

    public TaskWithEverythingForUpdateMapper(String taskTableName, String projectTableName,
            ProjectMemberMeta reporterMemberMeta, UserMeta reporterUserMeta,
            ProjectMemberMeta assigneeMemberMeta, UserMeta assigneeUserMeta,
            String typeTableName, String priorityTableName, String statusTableName, String cardTableName) {
        super(taskTableName);
        projectMapper = new ShortProjectMapper(projectTableName);
        reporterMapper = new ProjectMemberWithUserMapper(reporterMemberMeta, reporterUserMeta);
        assigneeMapper = new ProjectMemberWithUserMapper(assigneeMemberMeta, assigneeUserMeta);
        typeMapper = new TaskTypeMapper(typeTableName);
        priorityMapper = new TaskPriorityMapper(priorityTableName);
        statusMapper = new TaskStatusMapper(statusTableName);
        cardMapper = new TaskCardStatusMapper(cardTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskMeta.id) + ", " +
                generateSelectColumnStatement(TaskMeta.number) + ", " +
                generateSelectColumnStatement(TaskMeta.summary) + ", " +
                generateSelectColumnStatement(TaskMeta.recordVersion) + ", " +
                generateSelectColumnStatement(TaskMeta.descriptionMd) + ", " +
                generateSelectColumnStatement(TaskMeta.descriptionHtml) + ", " +
                generateSelectColumnStatement(TaskMeta.createdAt) + ", " +
                generateSelectColumnStatement(TaskMeta.updatedAt) + ", " +
                generateSelectColumnStatement(TaskMeta.hidden) + ", " +
                projectMapper.generateSelectColumnsStatement() + ", " +
                reporterMapper.generateSelectColumnsStatement() + ", " +
                assigneeMapper.generateSelectColumnsStatement() + ", " +
                typeMapper.generateSelectColumnsStatement() + ", " +
                priorityMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement() + ", " +
                cardMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong(generateColumnAlias(TaskMeta.id)));
        task.setNumber(rs.getLong(generateColumnAlias(TaskMeta.number)));
        task.setSummary(rs.getString(generateColumnAlias(TaskMeta.summary)));
        task.setRecordVersion(rs.getLong(generateColumnAlias(TaskMeta.recordVersion)));
        task.setDescriptionMd(rs.getString(generateColumnAlias(TaskMeta.descriptionMd)));
        task.setDescriptionHtml(rs.getString(generateColumnAlias(TaskMeta.descriptionHtml)));
        task.setCreatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.createdAt)));
        task.setUpdatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.updatedAt)));
        task.setHidden(rs.getBoolean(generateColumnAlias(TaskMeta.hidden)));

        task.setProject(projectMapper.mapRow(rs, rowNum));
        task.setReporter(reporterMapper.mapRow(rs, rowNum));
        if (rs.getObject(assigneeMapper.generateColumnAlias(ProjectMemberMeta.id)) != null) {
            task.setAssignee(assigneeMapper.mapRow(rs, rowNum));
        }
        task.setType(typeMapper.mapRow(rs, rowNum));
        task.setPriority(priorityMapper.mapRow(rs, rowNum));
        task.setStatus(statusMapper.mapRow(rs, rowNum));
        if (rs.getObject(cardMapper.generateColumnAlias(TaskCardMeta.id)) != null) {
            task.setCard(cardMapper.mapRow(rs, rowNum));
        }

        return task;
    }
}
