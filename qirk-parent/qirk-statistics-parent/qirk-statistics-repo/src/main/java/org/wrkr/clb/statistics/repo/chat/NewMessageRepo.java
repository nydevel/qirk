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
package org.wrkr.clb.statistics.repo.chat;

import java.sql.Timestamp;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.chat.NewMessage_;

@Repository
public class NewMessageRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + NewMessage_.TABLE_NAME + " " +
            "(" + NewMessage_.ownerType + ", " + // 1
            NewMessage_.ownerId + ", " + // 2
            NewMessage_.createdAt + ") " + // 3
            "VALUES (?, ?, ?);";

    public void save(@NotNull(message = "ownerType in NewMessageRepo must not be null") String ownerType,
            @NotNull(message = "ownerId in NewMessageRepo must not be null") Long ownerId,
            @NotNull(message = "createdAt in NewMessageRepo must not be null") Long createdAt) {
        getJdbcTemplate().update(INSERT,
                ownerType, ownerId, Timestamp.from(Instant.ofEpochMilli(createdAt)));
    }
}
