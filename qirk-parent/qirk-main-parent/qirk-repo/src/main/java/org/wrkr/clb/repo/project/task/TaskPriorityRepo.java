package org.wrkr.clb.repo.project.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskPriority.Priority;
import org.wrkr.clb.model.project.task.TaskPriorityMeta;
import org.wrkr.clb.repo.EnumEntityRepo;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.task.TaskPriorityMapper;

@Repository
public class TaskPriorityRepo extends JDBCBaseMainRepo implements EnumEntityRepo<TaskPriority, TaskPriority.Priority> {

    private static final TaskPriorityMapper PRIORITY_MAPPER = new TaskPriorityMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            PRIORITY_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskPriorityMeta.TABLE_NAME + " " +
            "WHERE " + TaskPriorityMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            PRIORITY_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskPriorityMeta.TABLE_NAME + " " +
            "WHERE " + TaskPriorityMeta.nameCode + " = ?;"; // 1

    private static final String SELECT_AND_ORDER_BY_IMPORTANCE_DESC = "SELECT " +
            PRIORITY_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskPriorityMeta.TABLE_NAME + " " +
            "ORDER BY " + TaskPriorityMeta.importance + " DESC;";

    public TaskPriority get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, PRIORITY_MAPPER, statusId);
    }

    @Override
    public TaskPriority getByNameCode(Priority status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, PRIORITY_MAPPER, status.toString());
    }

    public List<TaskPriority> listAndOrderDescByImportanceDesc() {
        return queryForList(SELECT_AND_ORDER_BY_IMPORTANCE_DESC, PRIORITY_MAPPER);
    }

    public Map<String, TaskPriority> mapNameCodeToPriority() {
        Map<String, TaskPriority> nameCodeToPriority = new HashMap<String, TaskPriority>();
        List<TaskPriority> priorities = queryForList(SELECT_AND_ORDER_BY_IMPORTANCE_DESC, PRIORITY_MAPPER);
        for (TaskPriority priority : priorities) {
            nameCodeToPriority.put(priority.getNameCode().toString(), priority);
        }
        return nameCodeToPriority;
    }
}
