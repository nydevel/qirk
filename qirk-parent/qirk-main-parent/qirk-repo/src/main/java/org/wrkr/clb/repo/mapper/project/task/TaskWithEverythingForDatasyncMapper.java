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
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskHashtagMeta;
import org.wrkr.clb.model.project.task.TaskMeta;

public class TaskWithEverythingForDatasyncMapper extends BaseMapper<Task> {

    private TaskTypeMapper typeMapper;
    private TaskPriorityMapper priorityMapper;
    private TaskStatusMapper statusMapper;

    private TaskHashtagMapper hashtagMapper;

    public TaskWithEverythingForDatasyncMapper(String taskTableName,
            String typeTableName, String priorityTableName, String statusTableName,
            String hashtagTableName) {
        super(taskTableName);
        typeMapper = new TaskTypeMapper(typeTableName);
        priorityMapper = new TaskPriorityMapper(priorityTableName);
        statusMapper = new TaskStatusMapper(statusTableName);
        hashtagMapper = new TaskHashtagMapper(hashtagTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskMeta.id) + ", " +
                generateSelectColumnStatement(TaskMeta.projectId) + ", " +
                generateSelectColumnStatement(TaskMeta.number) + ", " +
                generateSelectColumnStatement(TaskMeta.summary) + ", " +
                generateSelectColumnStatement(TaskMeta.descriptionMd) + ", " +
                generateSelectColumnStatement(TaskMeta.reporterId) + ", " +
                generateSelectColumnStatement(TaskMeta.assigneeId) + ", " +
                generateSelectColumnStatement(TaskMeta.createdAt) + ", " +
                generateSelectColumnStatement(TaskMeta.updatedAt) + ", " +
                generateSelectColumnStatement(TaskMeta.cardId) + ", " +
                generateSelectColumnStatement(TaskMeta.hidden) + ", " +
                typeMapper.generateSelectColumnsStatement() + ", " +
                priorityMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement() + ", " +
                hashtagMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong(generateColumnAlias(TaskMeta.id)));
        task.setProjectId(rs.getLong(generateColumnAlias(TaskMeta.projectId)));
        task.setNumber(rs.getLong(generateColumnAlias(TaskMeta.number)));
        task.setSummary(rs.getString(generateColumnAlias(TaskMeta.summary)));
        task.setDescriptionMd(rs.getString(generateColumnAlias(TaskMeta.descriptionMd)));
        task.setReporterId(rs.getLong(generateColumnAlias(TaskMeta.reporterId)));
        task.setAssigneeId((Long) rs.getObject(generateColumnAlias(TaskMeta.assigneeId))); // nullable
        task.setCreatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.createdAt)));
        task.setUpdatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.updatedAt)));
        task.setCardId((Long) rs.getObject(generateColumnAlias(TaskMeta.cardId))); // nullable
        task.setHidden(rs.getBoolean(generateColumnAlias(TaskMeta.hidden)));

        task.setType(typeMapper.mapRow(rs, rowNum));
        task.setPriority(priorityMapper.mapRow(rs, rowNum));
        task.setStatus(statusMapper.mapRow(rs, rowNum));

        if (rs.getObject(hashtagMapper.generateColumnAlias(TaskHashtagMeta.id)) != null) {
            task.getHashtags().add(hashtagMapper.mapRow(rs, rowNum));
        }

        return task;
    }

    public Task mapRow(Map<String, Object> result) {
        Task task = new Task();

        task.setId((Long) result.get(generateColumnAlias(TaskMeta.id)));
        task.setProjectId((Long) result.get(generateColumnAlias(TaskMeta.projectId)));
        task.setNumber((Long) result.get(generateColumnAlias(TaskMeta.number)));
        task.setSummary((String) result.get(generateColumnAlias(TaskMeta.summary)));
        task.setDescriptionMd((String) result.get(generateColumnAlias(TaskMeta.descriptionMd)));
        task.setReporterId((Long) result.get(generateColumnAlias(TaskMeta.reporterId)));
        task.setAssigneeId((Long) result.get(generateColumnAlias(TaskMeta.assigneeId)));
        task.setCreatedAt(getOffsetDateTime(result, generateColumnAlias(TaskMeta.createdAt)));
        task.setUpdatedAt(getOffsetDateTime(result, generateColumnAlias(TaskMeta.updatedAt)));
        task.setCardId((Long) result.get(generateColumnAlias(TaskMeta.cardId)));
        task.setHidden((Boolean) result.get(generateColumnAlias(TaskMeta.hidden)));

        task.setType(typeMapper.mapRow(result));
        task.setPriority(priorityMapper.mapRow(result));
        task.setStatus(statusMapper.mapRow(result));

        if (result.get(hashtagMapper.generateColumnAlias(TaskHashtagMeta.id)) != null) {
            task.getHashtags().add(mapRowForHashtag(result));
        }

        return task;
    }

    public TaskHashtag mapRowForHashtag(Map<String, Object> result) {
        return hashtagMapper.mapRow(result);
    }
}
