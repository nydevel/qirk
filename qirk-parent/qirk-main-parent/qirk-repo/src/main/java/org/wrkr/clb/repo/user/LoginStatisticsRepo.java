/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
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

    private static final String SELECT_1_BY_USER_ID = "SELECT 1 FROM " + LoginStatisticsMeta.TABLE_NAME + " " +
            "WHERE " + LoginStatisticsMeta.userId + " = ?;"; // 1

    public void save(LoginStatistics statistics) {
        getJdbcTemplate().update(INSERT,
                statistics.getInternetAddress(), statistics.getUser().getId(),
                Timestamp.from(statistics.getLoginAt().toInstant()));
    }

    public boolean existsByUserId(Long userId) {
        return exists(SELECT_1_BY_USER_ID, userId);
    }
}
