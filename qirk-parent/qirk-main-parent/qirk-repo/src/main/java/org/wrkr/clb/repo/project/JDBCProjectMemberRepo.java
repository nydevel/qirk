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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.organization.OrganizationMeta;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.ProjectMemberWithOrgMemberAndProjectMapper;

@Repository
public class JDBCProjectMemberRepo extends JDBCBaseMainRepo {

    private static final ProjectMemberWithOrgMemberAndProjectMapper PROJECT_MEMBER_WITH_ORG_MEMBER_AND_PROJECT_MAPPER = new ProjectMemberWithOrgMemberAndProjectMapper(
            ProjectMemberMeta.TABLE_NAME, OrganizationMemberMeta.TABLE_NAME, ProjectMeta.TABLE_NAME, OrganizationMeta.TABLE_NAME);

    private static final String SELECT_NOT_FIRED_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG_PREFIX = "SELECT " +
            PROJECT_MEMBER_WITH_ORG_MEMBER_AND_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.organizationMemberId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "WHERE NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + " ";

    private static final String SELECT_NOT_FIRED_BY_ID_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG = "" +
            SELECT_NOT_FIRED_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG_PREFIX +
            "AND " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.id + " = ?;"; // 1

    private static final String SELECT_NOT_FIRED_BY_USER_ID_AND_PROJECT_ID_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG = "" +
            SELECT_NOT_FIRED_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG_PREFIX +
            "AND " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.userId + " = ? " + // 1
            "AND " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " = ?;"; // 2

    private static final String SELECT_PROJECT_IDS_BY_NOT_FIRED_ORG_MEMBER_ID = "SELECT " +
            ProjectMemberMeta.projectId + " " +
            "FROM " + ProjectMemberMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMemberMeta.organizationMemberId + " = ? " + // 1
            "AND NOT " + ProjectMemberMeta.fired + ";";

    private static final String SELECT_PROJECT_IDS_BY_NOT_FIRED_USER_ID = "SELECT " +
            ProjectMemberMeta.projectId + " " +
            "FROM " + ProjectMemberMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMemberMeta.userId + " = ? " + // 1
            "AND NOT " + ProjectMemberMeta.fired + ";";

    private static final String SET_FIRED_TRUE_AND_FIRED_AT_NOW_FOR_NOT_FIRED_PREFIX = "UPDATE " +
            ProjectMemberMeta.TABLE_NAME + " " +
            "SET " + ProjectMemberMeta.fired + " = true, " +
            ProjectMemberMeta.firedAt + " = NOW() " +
            "WHERE NOT " + ProjectMemberMeta.fired + " ";

    private static final String SET_FIRED_TRUE_AND_FIRED_AT_NOW_FOR_NOT_FIRED_BY_ID = "" +
            SET_FIRED_TRUE_AND_FIRED_AT_NOW_FOR_NOT_FIRED_PREFIX +
            "AND " + ProjectMemberMeta.id + " = ?;"; // 1

    private static final String SET_FIRED_TRUE_AND_FIRED_AT_NOW_FOR_NOT_FIRED_BY_ORGANIZATION_MEMBER_ID = "" +
            SET_FIRED_TRUE_AND_FIRED_AT_NOW_FOR_NOT_FIRED_PREFIX +
            "AND " + ProjectMemberMeta.organizationMemberId + " = ?;"; // 1

    private static final String DELETE_NOT_FIRED_BY_ID = "DELETE FROM " +
            ProjectMemberMeta.TABLE_NAME + " " +
            "WHERE NOT " + ProjectMemberMeta.fired + " " +
            "AND " + ProjectMemberMeta.id + " = ?;"; // 2

    private static final String DELETE_NOT_FIRED_BY_HIRED_AT_AND_ORGANIZATION_MEMBER_ID = "DELETE FROM " +
            ProjectMemberMeta.TABLE_NAME + " " +
            "WHERE NOT " + ProjectMemberMeta.fired + " " +
            "AND " + ProjectMemberMeta.hiredAt + " >= ? " + // 1
            "AND " + ProjectMemberMeta.organizationMemberId + " = ?;"; // 2

    public ProjectMember getNotFiredByIdAndFetchOrganizationMemberAndProject(Long projectMemberId) {
        return queryForObjectOrNull(SELECT_NOT_FIRED_BY_ID_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG,
                PROJECT_MEMBER_WITH_ORG_MEMBER_AND_PROJECT_MAPPER,
                projectMemberId);
    }

    public ProjectMember getNotFiredByUserIdAndProjectIdAndFetchOrgMemberAndProjectAndOrganization(Long userId, Long projectId) {
        return queryForObjectOrNull(SELECT_NOT_FIRED_BY_USER_ID_AND_PROJECT_ID_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG,
                PROJECT_MEMBER_WITH_ORG_MEMBER_AND_PROJECT_MAPPER,
                userId, projectId);
    }

    public List<Long> listProjectIdsByNotFiredOrgMemberId(Long organizationMemberId) {
        return getJdbcTemplate().queryForList(SELECT_PROJECT_IDS_BY_NOT_FIRED_ORG_MEMBER_ID, Long.class, organizationMemberId);
    }

    public List<Long> listProjectIdsByNotFiredUserId(Long userId) {
        return queryForList(SELECT_PROJECT_IDS_BY_NOT_FIRED_USER_ID, Long.class, userId);
    }

    public void setFiredTrueAndFiredAtNowForNotFiredById(Long projectMemberId) {
        updateSingleRow(SET_FIRED_TRUE_AND_FIRED_AT_NOW_FOR_NOT_FIRED_BY_ID, projectMemberId);
    }

    public void setFiredToTrueAndFiredAtToNowForNotFiredByOrganizationMemberId(Long organizationMemberId) {
        getJdbcTemplate().update(SET_FIRED_TRUE_AND_FIRED_AT_NOW_FOR_NOT_FIRED_BY_ORGANIZATION_MEMBER_ID,
                organizationMemberId);
    }

    public void deleteNotFiredById(Long projectMemberId) {
        updateSingleRow(DELETE_NOT_FIRED_BY_ID, projectMemberId);
    }

    public void deleteNotFiredByHiredAtAndOrganizationMemberId(Long hiredAtSince, Long organizationMemberId) {
        getJdbcTemplate().update(DELETE_NOT_FIRED_BY_HIRED_AT_AND_ORGANIZATION_MEMBER_ID,
                Timestamp.from(Instant.ofEpochMilli(hiredAtSince)), organizationMemberId);
    }
}
