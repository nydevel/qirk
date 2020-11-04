package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskStatusMeta;

public class TaskStatusMapper extends BaseMapper<TaskStatus> {

    public TaskStatusMapper() {
        super();
    }

    public TaskStatusMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskStatusMeta.id) + ", " +
                generateSelectColumnStatement(TaskStatusMeta.nameCode);
    }

    @Override
    public TaskStatus mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskStatus status = new TaskStatus();

        status.setId(rs.getLong(generateColumnAlias(TaskStatusMeta.id)));
        status.setNameCode(TaskStatus.Status.valueOf(rs.getString(generateColumnAlias(TaskStatusMeta.nameCode))));

        return status;
    }

    public TaskStatus mapRow(Map<String, Object> result) {
        TaskStatus status = new TaskStatus();

        status.setId((Long) result.get(generateColumnAlias(TaskStatusMeta.id)));
        status.setNameCode(TaskStatus.Status.valueOf((String) result.get(generateColumnAlias(TaskStatusMeta.nameCode))));

        return status;
    }
}
