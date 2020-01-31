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
package org.wrkr.clb.repo.security;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.organization.OrganizationMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.organization.OrganizationMapper;
import org.wrkr.clb.repo.mapper.organization.OrganizationWithOrgMemberMapper;

@Repository
public class SecurityOrganizationRepo extends JDBCBaseMainRepo {

    private static final OrganizationMapper ORGANIZATION_MAPPER = new OrganizationMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            ORGANIZATION_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " " +
            "WHERE " + OrganizationMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_UI_ID = "SELECT " +
            ORGANIZATION_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " " +
            "WHERE " + OrganizationMeta.uiId + " = ?;"; // 1

    private static final OrganizationMapper TABLENAME_ORGANIZATION_MAPPER = new OrganizationMapper(OrganizationMeta.TABLE_NAME);

    private static final String SELECT_BY_MEMBER_ID = "SELECT " +
            TABLENAME_ORGANIZATION_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " " +
            "WHERE " + OrganizationMemberMeta.id + " = ?;"; // 1

    private static final OrganizationWithOrgMemberMapper ORGANIZATION_WITH_MEMBER_MAPPER = new OrganizationWithOrgMemberMapper(
            OrganizationMeta.TABLE_NAME, OrganizationMemberMeta.TABLE_NAME);

    private static final String SELECT_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID_PREFIX = "SELECT " +
            ORGANIZATION_WITH_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " " +
            "AND " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + " ";

    private static final String SELECT_BY_ID_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID = "" +
            SELECT_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID_PREFIX +
            "WHERE " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " = ?;"; // 2

    private static final String SELECT_BY_UI_ID_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID = "" +
            SELECT_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID_PREFIX +
            "WHERE " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.uiId + " = ?;"; // 2

    private static final String OTHER_ORG_MEMBER_TABLE_ALIAS = "other_user_organization";
    private static final String SELECT_BY_OTHER_MEMBER_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID = "SELECT " +
            ORGANIZATION_WITH_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + OTHER_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " = " +
            OTHER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.organizationId + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " " +
            "AND " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + " " +
            "WHERE " + OTHER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.id + " = ?;"; // 2

    public Organization getById(Long organizationId) {
        return queryForObjectOrNull(SELECT_BY_ID, ORGANIZATION_MAPPER,
                organizationId);
    }

    public Organization getByUiId(String organizationUiId) {
        return queryForObjectOrNull(SELECT_BY_UI_ID, ORGANIZATION_MAPPER,
                organizationUiId);
    }

    public Organization getByMemberId(Long organizationMemberId) {
        return queryForObjectOrNull(SELECT_BY_MEMBER_ID, TABLENAME_ORGANIZATION_MAPPER,
                organizationMemberId);
    }

    public Organization getByIdAndFetchNotFiredOrganizationMemberByUserId(Long organizationId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID,
                ORGANIZATION_WITH_MEMBER_MAPPER,
                userId, organizationId);
    }

    public Organization getByUiIdAndFetchNotFiredOrganizationMemberByUserId(String organizationUiId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_UI_ID_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID,
                ORGANIZATION_WITH_MEMBER_MAPPER,
                userId, organizationUiId);
    }

    public Organization getByOtherMemberIdAndFetchNotFiredOrganizationMemberByUserId(Long organizationMemberId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_OTHER_MEMBER_AND_FETCH_NOT_FIRED_ORGANIZATION_MEMBER_BY_USER_ID,
                ORGANIZATION_WITH_MEMBER_MAPPER,
                userId, organizationMemberId);
    }
}
