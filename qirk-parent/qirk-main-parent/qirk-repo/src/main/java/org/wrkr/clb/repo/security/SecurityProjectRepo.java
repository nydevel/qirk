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
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInviteMeta;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectApplicationMeta;
import org.wrkr.clb.model.project.ProjectInviteMeta;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.RoadMeta;
import org.wrkr.clb.model.project.task.TaskCardMeta;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.security.SecurityProjectMapper;
import org.wrkr.clb.repo.mapper.security.SecurityProjectWithMembershipMapper;

@Repository
public class SecurityProjectRepo extends JDBCBaseMainRepo {

    private static final SecurityProjectMapper PROJECT_MAPPER = new SecurityProjectMapper();

    private static final String SELECT_BY_ID_FOR_SECURITY = "SELECT " +
            PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_UI_ID_FOR_SECURITY = "SELECT " +
            PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.uiId + " = ?;"; // 1

    private static final SecurityProjectMapper TABLENAME_SECURITY_PROJECT_MAPPER = new SecurityProjectMapper(
            ProjectMeta.TABLE_NAME);

    private static final String SELECT_BY_INVITE_ID_FOR_SECURITY = "SELECT " +
            TABLENAME_SECURITY_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectInviteMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectInviteMeta.TABLE_NAME + "." + ProjectInviteMeta.projectId + " " +
            "WHERE " + ProjectInviteMeta.TABLE_NAME + "." + ProjectInviteMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_GRANTED_PERMS_INVITE_TOKEN_ID_FOR_SECURITY = "SELECT " +
            TABLENAME_SECURITY_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + GrantedPermissionsProjectInviteMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            GrantedPermissionsProjectInviteMeta.TABLE_NAME + "." + GrantedPermissionsProjectInviteMeta.projectId + " " +
            "WHERE " + GrantedPermissionsProjectInviteMeta.TABLE_NAME + "." + GrantedPermissionsProjectInviteMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_APPLICATION_ID_FOR_SECURITY = "SELECT " +
            TABLENAME_SECURITY_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectApplicationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectApplicationMeta.TABLE_NAME + "." + ProjectApplicationMeta.projectId + " " +
            "WHERE " + ProjectApplicationMeta.TABLE_NAME + "." + ProjectApplicationMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_MEMBER_ID_FOR_SECURITY = "SELECT " +
            TABLENAME_SECURITY_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMemberMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " " +
            "WHERE " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_ROAD_ID_FOR_SECURITY = "SELECT " +
            TABLENAME_SECURITY_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + RoadMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            RoadMeta.TABLE_NAME + "." + RoadMeta.projectId + " " +
            "WHERE " + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_CARD_ID_FOR_SECURITY = "SELECT " +
            TABLENAME_SECURITY_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskCardMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.projectId + " " +
            "WHERE " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_TASK_ID_FOR_SECURITY = "SELECT " +
            TABLENAME_SECURITY_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final SecurityProjectWithMembershipMapper PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER = new SecurityProjectWithMembershipMapper(
            ProjectMeta.TABLE_NAME, OrganizationMemberMeta.TABLE_NAME, ProjectMemberMeta.TABLE_NAME);

    private static final String SELECT_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY_PREFIX = "SELECT "
            + PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
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
            "AND NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + " ";

    private static final String SELECT_BY_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = ""
            + SELECT_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY_PREFIX +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 2

    private static final String SELECT_BY_UI_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = ""
            + SELECT_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY_PREFIX +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ?;"; // 2

    private static final String SELECT_BY_INVITE_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = "SELECT "
            + PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectInviteMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectInviteMeta.TABLE_NAME + "." + ProjectInviteMeta.projectId + " " +
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
            "WHERE " + ProjectInviteMeta.TABLE_NAME + "." + ProjectInviteMeta.id + " = ?;"; // 2

    private static final String SELECT_BY_GRANTED_PERMS_INVITE_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = "SELECT "
            + PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + GrantedPermissionsProjectInviteMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            GrantedPermissionsProjectInviteMeta.TABLE_NAME + "." + GrantedPermissionsProjectInviteMeta.projectId + " " +
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
            "WHERE " + GrantedPermissionsProjectInviteMeta.TABLE_NAME + "." + GrantedPermissionsProjectInviteMeta.id + " = ?;"; // 2

    private static final String SELECT_BY_APPLICATION_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = "SELECT "
            + PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectApplicationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectApplicationMeta.TABLE_NAME + "." + ProjectApplicationMeta.projectId + " " +
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
            "WHERE " + ProjectApplicationMeta.TABLE_NAME + "." + ProjectApplicationMeta.id + " = ?;"; // 2

    private static final String OTHER_PROJECT_MEMBER_TABLE_ALIAS = "other_project_member";
    private static final String SELECT_BY_OTHER_MEMBER_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = "SELECT "
            + PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMemberMeta.TABLE_NAME + " AS " + OTHER_PROJECT_MEMBER_TABLE_ALIAS + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            OTHER_PROJECT_MEMBER_TABLE_ALIAS + "." + ProjectMemberMeta.projectId + " " +
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
            "WHERE " + OTHER_PROJECT_MEMBER_TABLE_ALIAS + "." + ProjectMemberMeta.id + " = ?;"; // 2

    private static final String SELECT_BY_ROAD_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = "SELECT "
            + PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + RoadMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            RoadMeta.TABLE_NAME + "." + RoadMeta.projectId + " " +
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
            "WHERE " + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " = ?;"; // 2

    private static final String SELECT_BY_CARD_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = "SELECT "
            + PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskCardMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.projectId + " " +
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
            "WHERE " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " = ?;"; // 2

    private static final String SELECT_BY_TASK_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY = "SELECT "
            + PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " " +
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
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 2

    public Project getByIdForSecurity(Long projectId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_SECURITY, PROJECT_MAPPER,
                projectId);
    }

    public Project getByUiIdForSecurity(String projectUiId) {
        return queryForObjectOrNull(SELECT_BY_UI_ID_FOR_SECURITY, PROJECT_MAPPER,
                projectUiId);
    }

    public Project getByInviteIdForSecurity(Long inviteId) {
        return queryForObjectOrNull(SELECT_BY_INVITE_ID_FOR_SECURITY, TABLENAME_SECURITY_PROJECT_MAPPER,
                inviteId);
    }

    public Project getByGrantedPermsInviteIdForSecurity(Long inviteId) {
        return queryForObjectOrNull(SELECT_BY_GRANTED_PERMS_INVITE_TOKEN_ID_FOR_SECURITY, TABLENAME_SECURITY_PROJECT_MAPPER,
                inviteId);
    }

    public Project getByApplicationIdForSecurity(Long applicationId) {
        return queryForObjectOrNull(SELECT_BY_APPLICATION_ID_FOR_SECURITY, TABLENAME_SECURITY_PROJECT_MAPPER,
                applicationId);
    }

    public Project getByMemberIdForSecurity(Long projectMemberId) {
        return queryForObjectOrNull(SELECT_BY_MEMBER_ID_FOR_SECURITY, TABLENAME_SECURITY_PROJECT_MAPPER,
                projectMemberId);
    }

    public Project getByRoadIdForSecurity(Long roadId) {
        return queryForObjectOrNull(SELECT_BY_ROAD_ID_FOR_SECURITY, TABLENAME_SECURITY_PROJECT_MAPPER,
                roadId);
    }

    public Project getByCardIdForSecurity(Long cardId) {
        return queryForObjectOrNull(SELECT_BY_CARD_ID_FOR_SECURITY, TABLENAME_SECURITY_PROJECT_MAPPER,
                cardId);
    }

    public Project getByTaskIdForSecurity(Long taskId) {
        return queryForObjectOrNull(SELECT_BY_TASK_ID_FOR_SECURITY, TABLENAME_SECURITY_PROJECT_MAPPER,
                taskId);
    }

    public Project getByIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(Long projectId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, projectId);
    }

    public Project getByUiIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(String projectUiId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_UI_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, projectUiId);
    }

    public Project getByInviteIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(Long inviteId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_INVITE_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, inviteId);
    }

    public Project getByGrantedPermsInviteIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(
            Long inviteId, Long userId) {
        return queryForObjectOrNull(
                SELECT_BY_GRANTED_PERMS_INVITE_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, inviteId);
    }

    public Project getByApplicationIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(Long applicationId,
            Long userId) {
        return queryForObjectOrNull(
                SELECT_BY_APPLICATION_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, applicationId);
    }

    public Project getByOtherMemberIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(
            Long projectMemberId, Long userId) {
        return queryForObjectOrNull(
                SELECT_BY_OTHER_MEMBER_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, projectMemberId);
    }

    public Project getByRoadIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(Long roadId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ROAD_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, roadId);
    }

    public Project getByCardIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(Long cardId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_CARD_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, cardId);
    }

    public Project getByTaskIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(Long taskId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_TASK_ID_AND_FETCH_NOT_FIRED_ORG_MEMBER_AND_PROJECT_MEMBER_BY_USER_ID_FOR_SECURITY,
                PROJECT_WITH_ORG_MEMBER_AND_PROJECT_MEMBER_MAPPER,
                userId, taskId);
    }
}
