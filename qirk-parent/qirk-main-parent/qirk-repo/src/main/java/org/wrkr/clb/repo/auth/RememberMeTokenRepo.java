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
package org.wrkr.clb.repo.auth;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.auth.RememberMeToken;
import org.wrkr.clb.model.auth.RememberMeTokenMeta;


@Repository
public class RememberMeTokenRepo extends BaseAuthRepo {

    private static final String INSERT = "INSERT INTO " + RememberMeTokenMeta.TABLE_NAME + " " +
            "(" + RememberMeTokenMeta.token + ", " + // 1
            RememberMeTokenMeta.userId + ", " + // 2
            RememberMeTokenMeta.createdAt + ", " + // 3
            RememberMeTokenMeta.updatedAt + ") " + // 4
            "VALUES (?, ?, ?, ?);";

    private static final String SELECT_USER_ID_BY_TOKEN = "SELECT " + RememberMeTokenMeta.userId + " " +
            "FROM " + RememberMeTokenMeta.TABLE_NAME + " " +
            "WHERE " + RememberMeTokenMeta.token + " = ?;"; // 1

    private static final String UPDATE_UPDATED_AT_BY_TOKEN = "UPDATE " + RememberMeTokenMeta.TABLE_NAME + " " +
            "SET " + RememberMeTokenMeta.updatedAt + " = ? " + // 1
            "WHERE " + RememberMeTokenMeta.token + " = ?;"; // 2

    public void save(RememberMeToken token) {
        getJdbcTemplate().update(INSERT,
                token.getToken(), token.getUserId(), token.getCreatedAt(), token.getUpdatedAt());
    }

    public Long getUserIdByToken(String token) {
        return queryForObjectOrNull(SELECT_USER_ID_BY_TOKEN, Long.class, token);
    }

    public void updateUpdatedAtByToken(OffsetDateTime updatedAt, String token) {
        updateSingleRow(UPDATE_UPDATED_AT_BY_TOKEN, updatedAt, token);
    }
}
