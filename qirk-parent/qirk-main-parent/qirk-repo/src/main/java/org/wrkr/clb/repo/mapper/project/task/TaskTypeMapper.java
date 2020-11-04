package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.project.task.TaskTypeMeta;

public class TaskTypeMapper extends BaseMapper<TaskType> {

    public TaskTypeMapper() {
        super();
    }

    public TaskTypeMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskTypeMeta.id) + ", " +
                generateSelectColumnStatement(TaskTypeMeta.nameCode);
    }

    @Override
    public TaskType mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskType type = new TaskType();

        type.setId(rs.getLong(generateColumnAlias(TaskTypeMeta.id)));
        type.setNameCode(TaskType.Type.valueOf(rs.getString(generateColumnAlias(TaskTypeMeta.nameCode))));

        return type;
    }

    public TaskType mapRow(Map<String, Object> result) {
        TaskType type = new TaskType();

        type.setId((Long) result.get(generateColumnAlias(TaskTypeMeta.id)));
        type.setNameCode(TaskType.Type.valueOf((String) result.get(generateColumnAlias(TaskTypeMeta.nameCode))));

        return type;
    }
}
