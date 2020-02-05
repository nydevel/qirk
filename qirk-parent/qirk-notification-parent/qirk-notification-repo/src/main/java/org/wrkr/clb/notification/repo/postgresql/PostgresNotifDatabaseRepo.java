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
package org.wrkr.clb.notification.repo.postgresql;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.notification.repo.NotifDatabaseRepo;

@Repository
public class PostgresNotifDatabaseRepo extends BaseNotifRepo implements NotifDatabaseRepo {

    private static final String SELECT_1 = "SELECT 1;";

    @Override
    public void check() {
        getJdbcTemplate().queryForList(SELECT_1);
    }
}
