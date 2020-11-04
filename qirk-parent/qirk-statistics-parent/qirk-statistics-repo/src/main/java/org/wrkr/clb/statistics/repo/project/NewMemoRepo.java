package org.wrkr.clb.statistics.repo.project;

import java.sql.Timestamp;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.project.NewMemo_;

@Repository
public class NewMemoRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + NewMemo_.TABLE_NAME + " " +
            "(" + NewMemo_.authorUserId + ", " + // 1
            NewMemo_.createdAt + ") " + // 2
            "VALUES (?, ?);";

    public void save(@NotNull(message = "authorUserId in NewMemoRepo must not be null") Long authorUserId,
            @NotNull(message = "createdAt in NewMemoRepo must not be null") Long createdAt) {
        getJdbcTemplate().update(INSERT,
                authorUserId, Timestamp.from(Instant.ofEpochMilli(createdAt)));
    }
}
