package org.wrkr.clb.statistics.repo.project;

import java.sql.Timestamp;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.project.TaskUpdate_;


@Repository
@Validated
public class TaskUpdateRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + TaskUpdate_.TABLE_NAME + " " +
            "(" + TaskUpdate_.projectId + ", " + // 1
            TaskUpdate_.projectName + ", " + // 2
            TaskUpdate_.taskId + ", " + // 1
            TaskUpdate_.updatedAt + ") " + // 2
            "VALUES (?, ?, ?, ?);";

    private static final String COUNT = generateSelectCountAllStatement(TaskUpdate_.TABLE_NAME);

    public void save(@NotNull(message = "projectId in NewTaskRepo must not be null") Long projectId,
            @NotNull(message = "projectName in NewTaskRepo must not be null") String projectName,
            @NotNull(message = "taskId in TaskUpdateRepo must not be null") Long taskId,
            @NotNull(message = "updatedAt in TaskUpdateRepo must not be null") Long updatedAt) {
        getJdbcTemplate().update(INSERT,
                projectId, projectName, taskId, Timestamp.from(Instant.ofEpochMilli(updatedAt)));
    }

    public Long count() {
        return queryForObjectOrNull(COUNT, Long.class);
    }
}
