package org.wrkr.clb.repo.project.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskStatus.Status;
import org.wrkr.clb.model.project.task.TaskStatusMeta;
import org.wrkr.clb.repo.EnumEntityRepo;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.task.TaskStatusMapper;

@Repository
public class TaskStatusRepo extends JDBCBaseMainRepo implements EnumEntityRepo<TaskStatus, TaskStatus.Status> {

    private static final TaskStatusMapper STATUS_MAPPER = new TaskStatusMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskStatusMeta.TABLE_NAME + " " +
            "WHERE " + TaskStatusMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskStatusMeta.TABLE_NAME + " " +
            "WHERE " + TaskStatusMeta.nameCode + " = ?;"; // 1

    private static final String SELECT = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskStatusMeta.TABLE_NAME + ";";

    public TaskStatus get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, STATUS_MAPPER, statusId);
    }

    @Override
    public TaskStatus getByNameCode(Status status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, STATUS_MAPPER, status.toString());
    }

    public List<TaskStatus> list() {
        return queryForList(SELECT, STATUS_MAPPER);
    }

    public Map<String, TaskStatus> mapNameCodeToStatus() {
        Map<String, TaskStatus> nameCodeToStatus = new HashMap<String, TaskStatus>();
        List<TaskStatus> statuses = queryForList(SELECT, STATUS_MAPPER);
        for (TaskStatus status : statuses) {
            nameCodeToStatus.put(status.getNameCode().toString(), status);
        }
        return nameCodeToStatus;
    }
}
