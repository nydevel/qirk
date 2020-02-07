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
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.PasswordActivationTokenMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.user.PasswordActivationTokenMapper;
import org.wrkr.clb.repo.mapper.user.PasswordActivationTokenWithUserMapper;

@Repository
public class PasswordActivationTokenRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + PasswordActivationTokenMeta.TABLE_NAME + " " +
            "(" + PasswordActivationTokenMeta.token + ", " + // 1
            PasswordActivationTokenMeta.userId + ", " + // 2
            PasswordActivationTokenMeta.createdAt + ") " + // 3
            "VALUES (?, ?, ?);";

    private static final String EXISTS_BY_TOKEN = "SELECT 1 FROM " + PasswordActivationTokenMeta.TABLE_NAME + " " +
            "WHERE " + PasswordActivationTokenMeta.token + " = ?;"; // 1

    private static final String EXISTS_BY_USER_ID = "SELECT 1 FROM " + PasswordActivationTokenMeta.TABLE_NAME + " " +
            "WHERE " + PasswordActivationTokenMeta.userId + " = ?;"; // 1

    private static final PasswordActivationTokenMapper PASSWORD_ACTIVATION_TOKEN_MAPPER = new PasswordActivationTokenMapper();

    private static final String SELECT_BY_USER_ID = "SELECT " +
            PASSWORD_ACTIVATION_TOKEN_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + PasswordActivationTokenMeta.TABLE_NAME + " " +
            "WHERE " + PasswordActivationTokenMeta.userId + " = ?;"; // 1

    private static final PasswordActivationTokenWithUserMapper PASSWORD_ACTIVATION_TOKEN_WITH_USER_MAPPER = new PasswordActivationTokenWithUserMapper(
            PasswordActivationTokenMeta.TABLE_NAME, UserMeta.TABLE_NAME);

    private static final String SELECT_BY_TOKEN_AND_FETCH_USER = "SELECT " +
            PASSWORD_ACTIVATION_TOKEN_WITH_USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + PasswordActivationTokenMeta.TABLE_NAME + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + PasswordActivationTokenMeta.TABLE_NAME + "." + PasswordActivationTokenMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "WHERE " + PasswordActivationTokenMeta.TABLE_NAME + "." + PasswordActivationTokenMeta.token + " = ?;"; // 1

    private static final String DELETE = "DELETE FROM " + PasswordActivationTokenMeta.TABLE_NAME + " " +
            "WHERE " + PasswordActivationTokenMeta.id + " = ?;"; // 1

    public void save(PasswordActivationToken token) {
        getJdbcTemplate().update(INSERT,
                token.getToken(), token.getUserId(), Timestamp.from(token.getCreatedAt().toInstant()));
    }

    public boolean existsByToken(String token) {
        return exists(EXISTS_BY_TOKEN, token);
    }

    public boolean existsByUserId(Long userId) {
        return exists(EXISTS_BY_USER_ID, userId);
    }

    public PasswordActivationToken getByUserId(Long userId) {
        return queryForObjectOrNull(SELECT_BY_USER_ID, PASSWORD_ACTIVATION_TOKEN_MAPPER,
                userId);
    }

    public PasswordActivationToken getByTokenAndFetchUser(String token) {
        return queryForObjectOrNull(SELECT_BY_TOKEN_AND_FETCH_USER, PASSWORD_ACTIVATION_TOKEN_WITH_USER_MAPPER,
                token);
    }

    public void delete(PasswordActivationToken token) {
        updateSingleRow(DELETE, token.getId());
    }
}
