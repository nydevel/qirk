package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;

public class TaskNumberMapper extends BaseMapper<Task> {

    public TaskNumberMapper() {
        super();
    }

    public TaskNumberMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskMeta.id) + ", " +
                generateSelectColumnStatement(TaskMeta.number);
    }

    @Override
    public Task mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong(generateColumnAlias(TaskMeta.id)));
        task.setNumber(rs.getLong(generateColumnAlias(TaskMeta.number)));

        return task;
    }
}
