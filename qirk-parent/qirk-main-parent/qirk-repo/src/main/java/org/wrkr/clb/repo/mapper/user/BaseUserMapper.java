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
import org.wrkr.clb.repo.mapper.BaseEntityMapper;

public class BaseUserMapper extends BaseEntityMapper<User> {

    public BaseUserMapper() {
        super();
    }

    public BaseUserMapper(String tableName) {
        super(tableName);
    }

    public BaseUserMapper(UserMeta userMeta) {
        super(userMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(UserMeta.id) + ", " +
                generateSelectColumnStatement(UserMeta.manager);
    }

    @Override
    public User mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        User user = new User();

        user.setId(rs.getLong(generateColumnAlias(UserMeta.id)));
        user.setManager(rs.getBoolean(generateColumnAlias(UserMeta.manager)));

        return user;
    }

    public User mapRow(Map<String, Object> result) {
        User user = new User();

        user.setId((Long) result.get(generateColumnAlias(UserMeta.id)));
        user.setManager((boolean) result.get(generateColumnAlias(UserMeta.manager)));

        return user;
    }
}
