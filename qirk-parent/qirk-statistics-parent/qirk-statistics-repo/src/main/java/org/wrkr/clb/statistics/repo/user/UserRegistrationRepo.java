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
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.user.UserRegistration_;


@Repository
@Validated
public class UserRegistrationRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + UserRegistration_.TABLE_NAME + " " +
            "(" + UserRegistration_.uuid + ", " + // 1
            UserRegistration_.registeredAt + ", " + // 2
            UserRegistration_.userId + ") " + // 3
            "VALUES (?, ?, ?);";

    private static final String COUNT_USER_IDS = "SELECT COUNT(" + UserRegistration_.userId + ") " +
            "FROM " + UserRegistration_.TABLE_NAME + ";";

    public void save(@NotNull(message = "uuid in UserRegistrationRepo must not be null") String uuid,
            @NotNull(message = "registeredAt in UserRegistrationRepo must not be null") Long registeredAt,
            @NotNull(message = "userId in UserRegistrationRepo must not be null") Long userId) {
        getJdbcTemplate().update(INSERT,
                uuid, Timestamp.from(Instant.ofEpochMilli(registeredAt)), userId);
    }

    public Long countUserIds() {
        return queryForObjectOrNull(COUNT_USER_IDS, Long.class);
    }
}
