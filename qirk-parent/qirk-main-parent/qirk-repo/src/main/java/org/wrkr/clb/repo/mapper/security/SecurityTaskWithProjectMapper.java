package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;

public class SecurityTaskWithProjectMapper extends BaseMapper<Task> {

    protected SecurityProjectMapper projectMapper;

    protected SecurityTaskWithProjectMapper(String taskTableName) {
        super(taskTableName);
    }

    public SecurityTaskWithProjectMapper(String taskTableName, ProjectMeta projectMeta) {
        super(taskTableName);
        this.projectMapper = new SecurityProjectMapper(projectMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskMeta.id) + ", " +
                generateSelectColumnStatement(TaskMeta.projectId) + ", " +
                generateSelectColumnStatement(TaskMeta.reporterId) + ", " +
                generateSelectColumnStatement(TaskMeta.assigneeId) + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong(generateColumnAlias(TaskMeta.id)));
        task.setProjectId(rs.getLong(generateColumnAlias(TaskMeta.projectId)));
        task.setReporterId(rs.getLong(generateColumnAlias(TaskMeta.reporterId)));
        task.setAssigneeId((Long) rs.getObject(generateColumnAlias(TaskMeta.assigneeId))); // nullable

        task.setProject(projectMapper.mapRow(rs, rowNum));

        return task;
    }
}
