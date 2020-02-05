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

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.ProjectMemberWithProjectMapper;

@Repository
public class JDBCProjectMemberRepo extends JDBCBaseMainRepo {

    private static final ProjectMemberWithProjectMapper PROJECT_MEMBER_WITH_PROJECT_MAPPER = new ProjectMemberWithProjectMapper(
            ProjectMemberMeta.TABLE_NAME, ProjectMeta.TABLE_NAME);

    private static final String SELECT_NOT_FIRED_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG_PREFIX = "SELECT " +
            PROJECT_MEMBER_WITH_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + " ";

    private static final String SELECT_NOT_FIRED_BY_ID_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG = "" +
            SELECT_NOT_FIRED_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG_PREFIX +
            "AND " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.id + " = ?;"; // 1

    private static final String SELECT_NOT_FIRED_BY_USER_ID_AND_PROJECT_ID_AND_FETCH_ORG_MEMBER_AND_PROJECT = "" +
            SELECT_NOT_FIRED_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG_PREFIX +
            "AND " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.userId + " = ? " + // 1
            "AND " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " = ?;"; // 2

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

    private static final String DELETE_NOT_FIRED_BY_ID = "DELETE FROM " +
            ProjectMemberMeta.TABLE_NAME + " " +
            "WHERE NOT " + ProjectMemberMeta.fired + " " +
            "AND " + ProjectMemberMeta.id + " = ?;"; // 2

    public ProjectMember getNotFiredByIdAndFetchOrganizationMemberAndProject(Long projectMemberId) {
        return queryForObjectOrNull(SELECT_NOT_FIRED_BY_ID_AND_FETCH_ORG_MEMBER_AND_PROJECT_AND_ORG,
                PROJECT_MEMBER_WITH_PROJECT_MAPPER,
                projectMemberId);
    }

    public ProjectMember getNotFiredByUserIdAndProjectIdAndFetchOrgMemberAndProject(Long userId, Long projectId) {
        return queryForObjectOrNull(SELECT_NOT_FIRED_BY_USER_ID_AND_PROJECT_ID_AND_FETCH_ORG_MEMBER_AND_PROJECT,
                PROJECT_MEMBER_WITH_PROJECT_MAPPER,
                userId, projectId);
    }

    public List<Long> listProjectIdsByNotFiredUserId(Long userId) {
        return queryForList(SELECT_PROJECT_IDS_BY_NOT_FIRED_USER_ID, Long.class, userId);
    }

    public void setFiredTrueAndFiredAtNowForNotFiredById(Long projectMemberId) {
        updateSingleRow(SET_FIRED_TRUE_AND_FIRED_AT_NOW_FOR_NOT_FIRED_BY_ID, projectMemberId);
    }

    public void deleteNotFiredById(Long projectMemberId) {
        updateSingleRow(DELETE_NOT_FIRED_BY_ID, projectMemberId);
    }
}
