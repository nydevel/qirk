package org.wrkr.clb.chat.repo.sql;

import org.springframework.stereotype.Repository;


@Repository
public class SQLChatDatabaseRepo extends BaseChatRepo {

    private static final String SELECT_1 = "SELECT 1;";

    public void check() {
        getJdbcTemplate().queryForList(SELECT_1);
    }
}
