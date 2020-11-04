package org.wrkr.clb.statistics.repo.chat;

import java.sql.Timestamp;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.chat.NewMessage_;

@Repository
public class NewMessageRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + NewMessage_.TABLE_NAME + " " +
            "(" + NewMessage_.ownerType + ", " + // 1
            NewMessage_.ownerId + ", " + // 2
            NewMessage_.createdAt + ") " + // 3
            "VALUES (?, ?, ?);";

    public void save(@NotNull(message = "ownerType in NewMessageRepo must not be null") String ownerType,
            @NotNull(message = "ownerId in NewMessageRepo must not be null") Long ownerId,
            @NotNull(message = "createdAt in NewMessageRepo must not be null") Long createdAt) {
        getJdbcTemplate().update(INSERT,
                ownerType, ownerId, Timestamp.from(Instant.ofEpochMilli(createdAt)));
    }
}
