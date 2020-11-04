package org.wrkr.clb.test.repo;

import org.wrkr.clb.repo.JDBCBaseMainRepo;

public class JDBCTestRepo extends JDBCBaseMainRepo {

    public static final String DELETE_FROM_PREFIX = "DELETE FROM ";

    public void clearTable(String tableName) {
        getJdbcTemplate().update(DELETE_FROM_PREFIX + tableName + ";");
    }
}
