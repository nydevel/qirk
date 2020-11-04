package org.wrkr.clb.repo.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.user.FailedLoginAttempt;
import org.wrkr.clb.model.user.FailedLoginAttemptMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class FailedLoginAttemptRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + FailedLoginAttemptMeta.TABLE_NAME + " " +
            "(" + FailedLoginAttemptMeta.userId + ", " + // 1
            FailedLoginAttemptMeta.failedAt + ") " + // 2
            "VALUES (?, ?);";

    private static final String SELECT_TOP_BY_USER_ID_AND_ORDER_BY_FAILED_AT_DESC = "SELECT " +
            FailedLoginAttemptMeta.failedAt + " " +
            "FROM " + FailedLoginAttemptMeta.TABLE_NAME + " " +
            "WHERE " + FailedLoginAttemptMeta.userId + " = ? " + // 1
            "ORDER BY " + FailedLoginAttemptMeta.failedAt + " DESC " +
            "LIMIT ?;"; // 2

    private static final String DELETE_BY_USER_ID = "DELETE FROM " +
            FailedLoginAttemptMeta.TABLE_NAME + " " +
            "WHERE " + FailedLoginAttemptMeta.userId + " = ?;"; // 1

    public void save(FailedLoginAttempt attempt) {
        getJdbcTemplate().update(INSERT, attempt.getUserId(), attempt.getFailedAt());
    }

    public List<FailedLoginAttempt> listTopRecentByUserId(Long userId, int limit) {
        List<FailedLoginAttempt> result = new ArrayList<FailedLoginAttempt>(limit);
        for (long failedAt : queryForList(SELECT_TOP_BY_USER_ID_AND_ORDER_BY_FAILED_AT_DESC, Long.class, userId, limit)) {
            FailedLoginAttempt attempt = new FailedLoginAttempt();
            attempt.setUserId(userId);
            attempt.setFailedAt(failedAt);
            result.add(attempt);
        }
        return result;
    }

    public void deleteByUserId(Long userId) {
        getJdbcTemplate().update(DELETE_BY_USER_ID, userId);
    }
}
