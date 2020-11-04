package org.wrkr.clb.statistics.repo.project;

import java.sql.Timestamp;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.project.TaskTypeUpdate_;


@Repository
@Validated
public class TaskTypeUpdateRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + TaskTypeUpdate_.TABLE_NAME + " " +
            "(" + TaskTypeUpdate_.projectId + ", " + // 1
            TaskTypeUpdate_.projectName + ", " + // 2
            TaskTypeUpdate_.taskId + ", " + // 3
            TaskTypeUpdate_.updatedAt + ", " + // 4
            TaskTypeUpdate_.type + ") " + // 5
            "VALUES (?, ?, ?, ?, ?);";

    private static final String COUNT = generateSelectCountAllStatement(TaskTypeUpdate_.TABLE_NAME);

    public void save(@NotNull(message = "projectId in NewTaskRepo must not be null") Long projectId,
            @NotNull(message = "projectName in NewTaskRepo must not be null") String projectName,
            @NotNull(message = "taskId in TaskTypeUpdateRepo must not be null") Long taskId,
            @NotNull(message = "updatedAt in TaskTypeUpdateRepo must not be null") Long updatedAt,
            @NotNull(message = "type in TaskTypeUpdateRepo must not be null") String type) {
        getJdbcTemplate().update(INSERT,
                projectId, projectName, taskId, Timestamp.from(Instant.ofEpochMilli(updatedAt)), type);
    }

    public Long count() {
        return queryForObjectOrNull(COUNT, Long.class);
    }
}
