package org.wrkr.clb.repo;

import org.springframework.stereotype.Repository;

@Repository
public class MainDatabaseRepo extends JDBCBaseMainRepo {

    private static final String SELECT_1 = "SELECT 1;";

    public void check() {
        getJdbcTemplate().queryForList(SELECT_1);
    }
}
