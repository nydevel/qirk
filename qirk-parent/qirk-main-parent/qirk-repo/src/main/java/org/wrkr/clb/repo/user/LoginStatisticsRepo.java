package org.wrkr.clb.repo.user;

import java.sql.Timestamp;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.user.LoginStatistics;
import org.wrkr.clb.model.user.LoginStatisticsMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class LoginStatisticsRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + LoginStatisticsMeta.TABLE_NAME + " " +
            "(" + LoginStatisticsMeta.internetAddress + ", " + // 1
            LoginStatisticsMeta.userId + ", " + // 2
            LoginStatisticsMeta.loginAt + ") " + // 3
            "VALUES (?, ?, ?);";

    public void save(LoginStatistics statistics) {
        getJdbcTemplate().update(INSERT,
                statistics.getInternetAddress(), statistics.getUser().getId(),
                Timestamp.from(statistics.getLoginAt().toInstant()));
    }
}
