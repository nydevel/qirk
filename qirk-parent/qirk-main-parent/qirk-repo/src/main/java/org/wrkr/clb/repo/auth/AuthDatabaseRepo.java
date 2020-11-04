package org.wrkr.clb.repo.auth;

import org.springframework.stereotype.Repository;

@Repository
public class AuthDatabaseRepo extends BaseAuthRepo {

    private static final String SELECT_1 = "SELECT 1;";

    public void check() {
        getJdbcTemplate().queryForList(SELECT_1);
    }
}
