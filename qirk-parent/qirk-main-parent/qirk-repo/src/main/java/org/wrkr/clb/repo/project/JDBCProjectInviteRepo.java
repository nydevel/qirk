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
package org.wrkr.clb.repo.project;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectInviteMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class JDBCProjectInviteRepo extends JDBCBaseMainRepo {

    private static final String SELECT_PROJECT_IDS_BY_USER_ID_AND_STATUS_ID = "SELECT " +
            ProjectInviteMeta.projectId + " " +
            "FROM " + ProjectInviteMeta.TABLE_NAME + " " +
            "WHERE " + ProjectInviteMeta.userId + " = ? " + // 1
            "AND " + ProjectInviteMeta.statusId + " = ?;"; // 2

    public List<Long> listProjectIdsByUserIdAndStatusId(Long userId, Long statusId) {
        return queryForList(SELECT_PROJECT_IDS_BY_USER_ID_AND_STATUS_ID, Long.class, userId, statusId);
    }
}
