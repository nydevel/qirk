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
package org.wrkr.clb.statistics.repo.user;

import java.sql.Timestamp;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.user.FirstLogin_;


@Repository
public class FirstLoginRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + FirstLogin_.TABLE_NAME + " " +
            "(" + FirstLogin_.userId + ", " + // 1
            FirstLogin_.loginAt + ") " + // 3
            "VALUES (?, ?);";

    private static final String COUNT_USER_IDS = "SELECT COUNT(" + FirstLogin_.userId + ") " +
            "FROM " + FirstLogin_.TABLE_NAME + ";";

    public void save(@NotNull(message = "userId in FirstLoginRepo must not be null") Long userId,
            @NotNull(message = "loginAt in FirstLoginRepo must not be null") Long loginAt) {
        getJdbcTemplate().update(INSERT, userId, Timestamp.from(Instant.ofEpochMilli(loginAt)));
    }

    public Long countUserIds() {
        return queryForObjectOrNull(COUNT_USER_IDS, Long.class);
    }
}
