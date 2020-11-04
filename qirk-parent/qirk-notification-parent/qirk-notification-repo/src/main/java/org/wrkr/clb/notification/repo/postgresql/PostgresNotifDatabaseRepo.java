package org.wrkr.clb.notification.repo.postgresql;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.notification.repo.NotifDatabaseRepo;

@Repository
public class PostgresNotifDatabaseRepo extends BaseNotifRepo implements NotifDatabaseRepo {

    private static final String SELECT_1 = "SELECT 1;";

    @Override
    public void check() {
        getJdbcTemplate().queryForList(SELECT_1);
    }
}
