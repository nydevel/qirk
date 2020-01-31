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
package org.wrkr.clb.repo.project;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.MemoMeta;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.security.SecurityMemoWithProjectAndMembershipMapper;
import org.wrkr.clb.repo.mapper.security.SecurityMemoWithProjectMapper;

@Repository
public class JDBCMemoRepo extends JDBCBaseMainRepo {

    private static final SecurityMemoWithProjectMapper SECURITY_MEMO_MAPPER = new SecurityMemoWithProjectMapper(
            MemoMeta.TABLE_NAME, ProjectMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY = "SELECT " +
            SECURITY_MEMO_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + MemoMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + MemoMeta.TABLE_NAME + "." + MemoMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + MemoMeta.TABLE_NAME + "." + MemoMeta.id + " = ?;"; // 1

    private static final SecurityMemoWithProjectAndMembershipMapper SECURITY_MEMO_WITH_MEMBERSHIP_MAPPER = new SecurityMemoWithProjectAndMembershipMapper(
            MemoMeta.TABLE_NAME, ProjectMeta.TABLE_NAME, OrganizationMemberMeta.TABLE_NAME, ProjectMemberMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY = "SELECT "
            + SECURITY_MEMO_WITH_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + MemoMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + MemoMeta.TABLE_NAME + "." + MemoMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " " +
            "AND " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + " " +
            "LEFT JOIN " + ProjectMemberMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.organizationMemberId + " " +
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " " +
            "AND NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + " " +
            "WHERE " + MemoMeta.TABLE_NAME + "." + MemoMeta.id + " = ?;"; // 2

    public Memo getByIdAndFetchProjectForSecurity(Long memoId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY, SECURITY_MEMO_MAPPER,
                memoId);
    }

    public Memo getByIdAndFetchProjectAndMembershipByUserIdForSecurity(Long memoId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY,
                SECURITY_MEMO_WITH_MEMBERSHIP_MAPPER,
                userId, memoId);
    }
}
