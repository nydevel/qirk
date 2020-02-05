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
package org.wrkr.clb.repo.security;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.user.BaseUserMapper;

@Repository
public class SecurityUserRepo extends JDBCBaseMainRepo {

    private static final BaseUserMapper SECURITY_USER_MAPPER = new BaseUserMapper();

    private static final String SELECT_BY_ID_FOR_SECURITY = "SELECT " +
            SECURITY_USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    public User getByIdForSecurity(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_SECURITY, SECURITY_USER_MAPPER,
                userId);
    }
}
