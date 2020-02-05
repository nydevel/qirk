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
package org.wrkr.clb.repo;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.InviteStatus;
import org.wrkr.clb.model.InviteStatus.Status;
import org.wrkr.clb.model.InviteStatusMeta;
import org.wrkr.clb.repo.mapper.InviteStatusMapper;


@Repository
public class InviteStatusRepo extends JDBCBaseMainRepo implements EnumRepo<InviteStatus, InviteStatus.Status> {

    private static final InviteStatusMapper STATUS_MAPPER = new InviteStatusMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + InviteStatusMeta.TABLE_NAME + " " +
            "WHERE " + InviteStatusMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + InviteStatusMeta.TABLE_NAME + " " +
            "WHERE " + InviteStatusMeta.nameCode + " = ?;"; // 1

    public InviteStatus get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, STATUS_MAPPER, statusId);
    }

    @Override
    public InviteStatus getByNameCode(Status status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, STATUS_MAPPER, status.toString());
    }
}
