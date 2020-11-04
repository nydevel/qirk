package org.wrkr.clb.statistics.repo;

import org.springframework.stereotype.Repository;

@Repository
public class StatDatabaseRepo extends BaseStatRepo {

    private static final String SELECT_1 = "SELECT 1;";

    public void check() {
        getJdbcTemplate().queryForList(SELECT_1);
    }
}
