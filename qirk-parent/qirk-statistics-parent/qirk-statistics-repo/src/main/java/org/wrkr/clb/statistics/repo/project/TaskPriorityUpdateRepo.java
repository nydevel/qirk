package org.wrkr.clb.statistics.repo.project;

import java.sql.Timestamp;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.project.TaskPriorityUpdate_;


@Repository
@Validated
public class TaskPriorityUpdateRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + TaskPriorityUpdate_.TABLE_NAME + " " +
            "(" + TaskPriorityUpdate_.projectId + ", " + // 1
            TaskPriorityUpdate_.projectName + ", " + // 2
            TaskPriorityUpdate_.taskId + ", " + // 1
            TaskPriorityUpdate_.updatedAt + ", " + // 2
            TaskPriorityUpdate_.priority + ") " + // 3
            "VALUES (?, ?, ?, ?, ?);";

    private static final String COUNT = generateSelectCountAllStatement(TaskPriorityUpdate_.TABLE_NAME);

    public void save(@NotNull(message = "projectId in NewTaskRepo must not be null") Long projectId,
            @NotNull(message = "projectName in NewTaskRepo must not be null") String projectName,
            @NotNull(message = "taskId in TaskPriorityUpdateRepo must not be null") Long taskId,
            @NotNull(message = "updatedAt in TaskPriorityUpdateRepo must not be null") Long updatedAt,
            @NotNull(message = "priority in TaskPriorityUpdateRepo must not be null") String priority) {
        getJdbcTemplate().update(INSERT,
                projectId, projectName, taskId, Timestamp.from(Instant.ofEpochMilli(updatedAt)), priority);
    }

    public Long count() {
        return queryForObjectOrNull(COUNT, Long.class);
    }
}
