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
import org.wrkr.clb.statistics.repo.model.user.NewUser_;


@Repository
@Validated
public class NewUserRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + NewUser_.TABLE_NAME + " " +
            "(" + NewUser_.uuid + ", " + // 1
            NewUser_.visitedAt + ") " + // 2
            "VALUES (?, ?);";

    private static final String COUNT_UUIDS = "SELECT COUNT(DISTINCT(" + NewUser_.uuid + ")) " +
            "FROM " + NewUser_.TABLE_NAME + ";";

    public void save(@NotNull(message = "uuid in NewUserRepo must not be null") String uuid,
            @NotNull(message = "visitedAt in NewUserRepo must not be null") Long visitedAt) {
        getJdbcTemplate().update(INSERT,
                uuid, Timestamp.from(Instant.ofEpochMilli(visitedAt)));
    }

    public Long countUuids() {
        return queryForObjectOrNull(COUNT_UUIDS, Long.class);
    }
}
