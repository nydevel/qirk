package org.wrkr.clb.repo.project.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.project.task.TaskType.Type;
import org.wrkr.clb.model.project.task.TaskTypeMeta;
import org.wrkr.clb.repo.EnumEntityRepo;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.task.TaskTypeMapper;

@Repository
public class TaskTypeRepo extends JDBCBaseMainRepo implements EnumEntityRepo<TaskType, TaskType.Type> {

    private static final TaskTypeMapper TYPE_MAPPER = new TaskTypeMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            TYPE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskTypeMeta.TABLE_NAME + " " +
            "WHERE " + TaskTypeMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            TYPE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskTypeMeta.TABLE_NAME + " " +
            "WHERE " + TaskTypeMeta.nameCode + " = ?;"; // 1

    private static final String SELECT = "SELECT " +
            TYPE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskTypeMeta.TABLE_NAME + ";";

    public TaskType get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, TYPE_MAPPER, statusId);
    }

    @Override
    public TaskType getByNameCode(Type status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, TYPE_MAPPER, status.toString());
    }

    public List<TaskType> list() {
        return queryForList(SELECT, TYPE_MAPPER);
    }

    public Map<String, TaskType> mapNameCodeToType() {
        Map<String, TaskType> nameCodeToType = new HashMap<String, TaskType>();
        List<TaskType> types = queryForList(SELECT, TYPE_MAPPER);
        for (TaskType type : types) {
            nameCodeToType.put(type.getNameCode().toString(), type);
        }
        return nameCodeToType;
    }
}
