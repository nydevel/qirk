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
package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.user.PasswordActivationToken;

public class PasswordActivationTokenWithUserMapper extends PasswordActivationTokenMapper {

    private AccountUserMapper userMapper;

    public PasswordActivationTokenWithUserMapper(String activationTokenTableName, String userTableName) {
        super(activationTokenTableName);
        userMapper = new AccountUserMapper(userTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                userMapper.generateSelectColumnsStatement();
    }

    @Override
    public PasswordActivationToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        PasswordActivationToken token = super.mapRow(rs, rowNum);
        token.setUser(userMapper.mapRow(rs, rowNum));
        return token;
    }
}
