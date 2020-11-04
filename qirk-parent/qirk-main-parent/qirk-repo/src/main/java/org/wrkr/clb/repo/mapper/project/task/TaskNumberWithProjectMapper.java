package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.repo.mapper.project.ProjectNameAndUiIdMapper;

public class TaskNumberWithProjectMapper extends TaskNumberMapper {

    private ProjectNameAndUiIdMapper projectMapper;

    public TaskNumberWithProjectMapper(String taskTableName, String projectTableName) {
        super(taskTableName);
        projectMapper = new ProjectNameAndUiIdMapper(projectTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = super.mapRow(rs, rowNum);
        task.setProject(projectMapper.mapRow(rs, rowNum));
        return task;
    }
}
