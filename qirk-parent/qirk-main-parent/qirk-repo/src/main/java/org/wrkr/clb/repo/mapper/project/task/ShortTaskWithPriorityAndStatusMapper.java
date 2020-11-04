package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.model.project.task.Task;

public class ShortTaskWithPriorityAndStatusMapper extends ShortTaskMapper {

    private TaskPriorityMapper priorityMapper;
    private TaskStatusMapper statusMapper;

    public ShortTaskWithPriorityAndStatusMapper(String taskTableName, String priorityTableName, String statusTableName) {
        super(taskTableName);
        priorityMapper = new TaskPriorityMapper(priorityTableName);
        statusMapper = new TaskStatusMapper(statusTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                priorityMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = super.mapRow(rs, rowNum);

        task.setPriority(priorityMapper.mapRow(rs, rowNum));
        task.setStatus(statusMapper.mapRow(rs, rowNum));

        return task;
    }

    @Override
    public Task mapRow(Map<String, Object> result) {
        Task task = super.mapRow(result);

        task.setPriority(priorityMapper.mapRow(result));
        task.setStatus(statusMapper.mapRow(result));

        return task;
    }
}
