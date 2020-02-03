/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.repo;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.ApplicationStatus;
import org.wrkr.clb.model.ApplicationStatus.Status;
import org.wrkr.clb.model.ApplicationStatusMeta;
import org.wrkr.clb.repo.mapper.ApplicationStatusMapper;


@Repository
public class ApplicationStatusRepo extends JDBCBaseMainRepo implements EnumRepo<ApplicationStatus, ApplicationStatus.Status> {

    private static final ApplicationStatusMapper STATUS_MAPPER = new ApplicationStatusMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ApplicationStatusMeta.TABLE_NAME + " " +
            "WHERE " + ApplicationStatusMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ApplicationStatusMeta.TABLE_NAME + " " +
            "WHERE " + ApplicationStatusMeta.nameCode + " = ?;"; // 1

    public ApplicationStatus get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, STATUS_MAPPER, statusId);
    }

    @Override
    public ApplicationStatus getByNameCode(Status status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, STATUS_MAPPER, status.toString());
    }
}
