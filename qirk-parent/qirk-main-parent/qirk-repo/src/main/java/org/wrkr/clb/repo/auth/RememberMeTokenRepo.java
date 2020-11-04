package org.wrkr.clb.repo.auth;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.auth.RememberMeToken;
import org.wrkr.clb.model.auth.RememberMeTokenMeta;


@Repository
public class RememberMeTokenRepo extends BaseAuthRepo {

    private static final String INSERT = "INSERT INTO " + RememberMeTokenMeta.TABLE_NAME + " " +
            "(" + RememberMeTokenMeta.token + ", " + // 1
            RememberMeTokenMeta.userId + ", " + // 2
            RememberMeTokenMeta.createdAt + ", " + // 3
            RememberMeTokenMeta.updatedAt + ") " + // 4
            "VALUES (?, ?, ?, ?);";

    private static final String SELECT_USER_ID_BY_TOKEN = "SELECT " + RememberMeTokenMeta.userId + " " +
            "FROM " + RememberMeTokenMeta.TABLE_NAME + " " +
            "WHERE " + RememberMeTokenMeta.token + " = ?;"; // 1

    private static final String UPDATE_UPDATED_AT_BY_TOKEN = "UPDATE " + RememberMeTokenMeta.TABLE_NAME + " " +
            "SET " + RememberMeTokenMeta.updatedAt + " = ? " + // 1
            "WHERE " + RememberMeTokenMeta.token + " = ?;"; // 2

    public void save(RememberMeToken token) {
        getJdbcTemplate().update(INSERT,
                token.getToken(), token.getUserId(), token.getCreatedAt(), token.getUpdatedAt());
    }

    public Long getUserIdByToken(String token) {
        return queryForObjectOrNull(SELECT_USER_ID_BY_TOKEN, Long.class, token);
    }

    public void updateUpdatedAtByToken(OffsetDateTime updatedAt, String token) {
        updateSingleRow(UPDATE_UPDATED_AT_BY_TOKEN, updatedAt, token);
    }
}
