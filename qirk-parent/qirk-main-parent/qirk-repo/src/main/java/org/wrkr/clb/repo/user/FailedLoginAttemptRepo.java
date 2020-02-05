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
