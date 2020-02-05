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
import java.util.Map;

import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;

public class PublicUserMapper extends BaseUserMapper {

    public PublicUserMapper() {
        super();
    }

    public PublicUserMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(UserMeta.username) + ", " +
                generateSelectColumnStatement(UserMeta.fullName);
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);

        user.setUsername(rs.getString(generateColumnAlias(UserMeta.username)));
        user.setFullName(rs.getString(generateColumnAlias(UserMeta.fullName)));

        return user;
    }

    @Override
    public User mapRow(Map<String, Object> result) {
        User user = super.mapRow(result);

        user.setUsername((String) result.get(generateColumnAlias(UserMeta.username)));
        user.setFullName((String) result.get(generateColumnAlias(UserMeta.fullName)));

        return user;
    }
}
