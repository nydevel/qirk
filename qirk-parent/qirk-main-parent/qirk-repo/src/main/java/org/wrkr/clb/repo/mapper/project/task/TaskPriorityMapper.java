package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskPriorityMeta;

public class TaskPriorityMapper extends BaseMapper<TaskPriority> {

    public TaskPriorityMapper() {
        super();
    }

    public TaskPriorityMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskPriorityMeta.id) + ", " +
                generateSelectColumnStatement(TaskPriorityMeta.nameCode);
    }

    @Override
    public TaskPriority mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskPriority priority = new TaskPriority();

        priority.setId(rs.getLong(generateColumnAlias(TaskPriorityMeta.id)));
        priority.setNameCode(TaskPriority.Priority.valueOf(rs.getString(generateColumnAlias(TaskPriorityMeta.nameCode))));

        return priority;
    }

    public TaskPriority mapRow(Map<String, Object> result) {
        TaskPriority priority = new TaskPriority();

        priority.setId((Long) result.get(generateColumnAlias(TaskPriorityMeta.id)));
        priority.setNameCode(TaskPriority.Priority.valueOf((String) result.get(generateColumnAlias(TaskPriorityMeta.nameCode))));

        return priority;
    }
}
