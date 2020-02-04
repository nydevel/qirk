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

import java.sql.Array;
import java.sql.Connection;
import java.sql.JDBCType;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.organization.OrganizationMeta;
import org.wrkr.clb.model.project.DropboxSettingsMeta;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.RoadMeta;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProjectMeta;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.DropboxProjectMapper;
import org.wrkr.clb.repo.mapper.project.ProjectDocMapper;
import org.wrkr.clb.repo.mapper.project.ProjectNameAndUiIdMapper;
import org.wrkr.clb.repo.mapper.project.ProjectWithEverythingForReadAndSecurityMembershipMapper;
import org.wrkr.clb.repo.mapper.project.ProjectWithEverythingForReadMapper;

@Repository
public class JDBCProjectRepo extends JDBCBaseMainRepo implements InitializingBean {

    private static final String SELECT_PROJECT_ID_BY_UI_ID = "SELECT " + ProjectMeta.id + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.uiId + " = ?;"; // 1

    private static final String SELECT_ORGANIZATION_ID_BY_ID = "SELECT " + ProjectMeta.organizationId + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.id + " = ?;"; // 1

    private static final String SELECT_UI_ID_BY_ID = "SELECT " + ProjectMeta.uiId + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.id + " = ?;"; // 1

    private static final String SELECT_PROJECT_ID_BY_ROAD_ID = "SELECT " + RoadMeta.projectId + " " +
            "FROM " + RoadMeta.TABLE_NAME + " " +
            "WHERE " + RoadMeta.id + " = ?;"; // 1

    private static final ProjectNameAndUiIdMapper NAME_AND_UI_ID_MAPPER = new ProjectNameAndUiIdMapper(ProjectMeta.TABLE_NAME);

    private static final String SELECT_NAME_AND_UI_ID_BY_TASK_ID = "SELECT " +
            NAME_AND_UI_ID_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final ProjectDocMapper PROJECT_DOC_MAPPER = new ProjectDocMapper();

    private static final String SELECT_BY_ID_FOR_DOCUMENTATION = "SELECT " +
            PROJECT_DOC_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_UI_ID_FOR_DOCUMENTATION = "SELECT " +
            PROJECT_DOC_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.uiId + " = ?;"; // 1

    private static final String PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS = "proj_dropbox_settings";
    private static final String ORG_DROPBOX_SETTINGS_TABLE_ALIAS = "org_dropbox_settings";
    @Deprecated
    private static final DropboxProjectMapper DROPBOX_PROJECT_MAPPER = new DropboxProjectMapper(
            ProjectMeta.TABLE_NAME, PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS,
            OrganizationMeta.TABLE_NAME, ORG_DROPBOX_SETTINGS_TABLE_ALIAS);

    @Deprecated
    private static final String SELECT_AND_FETCH_DROPBOX_SETTINGS_PREFIX = "SELECT " +
            DROPBOX_PROJECT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "LEFT JOIN " + DropboxSettingsMeta.TABLE_NAME + " AS " + PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.dropboxSettingsId + " = " +
            PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS + "." + DropboxSettingsMeta.id + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "LEFT JOIN " + DropboxSettingsMeta.TABLE_NAME + " AS " + ORG_DROPBOX_SETTINGS_TABLE_ALIAS + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.dropboxSettingsId + " = " +
            ORG_DROPBOX_SETTINGS_TABLE_ALIAS + "." + DropboxSettingsMeta.id;

    @Deprecated
    private static final String SELECT_BY_ID_AND_FETCH_DROPBOX_SETTINGS = SELECT_AND_FETCH_DROPBOX_SETTINGS_PREFIX + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 1

    @Deprecated
    private static final String SELECT_BY_UI_ID_AND_FETCH_DROPBOX_SETTINGS = SELECT_AND_FETCH_DROPBOX_SETTINGS_PREFIX + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ?;"; // 1

    private static final ProjectWithEverythingForReadMapper PROJECT_WITH_EVERYTHING_FOR_READ_MAPPER = new ProjectWithEverythingForReadMapper(
            ProjectMeta.TABLE_NAME, OrganizationMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_WITH_EVERYTHING_FOR_READ = "SELECT " +
            PROJECT_WITH_EVERYTHING_FOR_READ_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "LEFT JOIN " + DropboxSettingsMeta.TABLE_NAME + " AS " + PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.dropboxSettingsId + " = " +
            PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS + "." + DropboxSettingsMeta.id + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "LEFT JOIN " + DropboxSettingsMeta.TABLE_NAME + " AS " + ORG_DROPBOX_SETTINGS_TABLE_ALIAS + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.dropboxSettingsId + " = " +
            ORG_DROPBOX_SETTINGS_TABLE_ALIAS + "." + DropboxSettingsMeta.id + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 1

    private static final ProjectWithEverythingForReadAndSecurityMembershipMapper PROJECT_WITH_EVERYTHING_FOR_READ_AND_SECURITY_MEMBERSHIP_MAPPER = new ProjectWithEverythingForReadAndSecurityMembershipMapper(
            ProjectMeta.TABLE_NAME, OrganizationMeta.TABLE_NAME,
            OrganizationMemberMeta.TABLE_NAME, ProjectMemberMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_WITH_EVERYTHING_FOR_READ_AND_FETCH_MEMBERSHIP_FOR_SECURITY = "SELECT " +
            PROJECT_WITH_EVERYTHING_FOR_READ_AND_SECURITY_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "LEFT JOIN " + DropboxSettingsMeta.TABLE_NAME + " AS " + PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.dropboxSettingsId + " = " +
            PROJECT_DROPBOX_SETTINGS_TABLE_ALIAS + "." + DropboxSettingsMeta.id + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "LEFT JOIN " + DropboxSettingsMeta.TABLE_NAME + " AS " + ORG_DROPBOX_SETTINGS_TABLE_ALIAS + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.dropboxSettingsId + " = " +
            ORG_DROPBOX_SETTINGS_TABLE_ALIAS + "." + DropboxSettingsMeta.id + " " +
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
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 2

    private static final String SELECT_PRIVATE_IDS_BY_ORGANIZATION_ID_AND_MIN_MEMBERS_COUNT = "SELECT " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON (" + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " " +
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + ") " +
            "LEFT JOIN " + ProjectMemberMeta.TABLE_NAME + " " +
            "ON (" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.organizationMemberId + " " +
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " " +
            "AND NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + ") " +
            "WHERE (" + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = ? " + // 1
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.isPrivate + " " +
            "AND (" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.manager + " " +
            "OR " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.id + " IS NOT NULL)) " +
            "GROUP BY " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "HAVING COUNT(DISTINCT(" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + ")) >= ?;"; // 2

    private static final String SELECT_FROZEN_PRIVATE_IDS_BY_ORGANIZATION_ID_AND_MIN_MEMBERS_COUNT = "SELECT " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON (" + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " " +
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + ") " +
            "LEFT JOIN " + ProjectMemberMeta.TABLE_NAME + " " +
            "ON (" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.organizationMemberId + " " +
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " " +
            "AND NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + ") " +
            "WHERE (" + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = ? " + // 1
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.isPrivate + " " +
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.frozen + " " +
            "AND (" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.manager + " " +
            "OR " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.id + " IS NOT NULL)) " +
            "GROUP BY " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "HAVING COUNT(DISTINCT(" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + ")) >= ?;"; // 2

    private static final ProjectNameAndUiIdMapper PROJECT_NAME_AND_UI_ID_MAPPER = new ProjectNameAndUiIdMapper(
            ProjectMeta.TABLE_NAME);

    private static final String SELECT_BY_ORGANIZATION_ID_INNER_JOIN_IMPORTED_JIRA_PROJECT = "SELECT " +
            PROJECT_NAME_AND_UI_ID_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ImportedJiraProjectMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ImportedJiraProjectMeta.TABLE_NAME + "." + ImportedJiraProjectMeta.projectId + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " = ?;";

    private static final String UPDATE_FROZEN_BY_ID = "UPDATE " + ProjectMeta.TABLE_NAME + " " +
            "SET " + ProjectMeta.frozen + " = ? " +
            "WHERE " + ProjectMeta.id + " = ?;";

    private static final String UPDATE_FROZEN_SET_TRUE_FOR_PRIVATE_BY_ORGANIZATION_ID_AND_IDS = "UPDATE " +
            ProjectMeta.TABLE_NAME + " " +
            "SET " + ProjectMeta.frozen + " = true " +
            "WHERE " + ProjectMeta.organizationId + " = ? " + // 1
            "AND " + ProjectMeta.isPrivate + " " +
            "AND NOT " + ProjectMeta.frozen + " " +
            "AND " + ProjectMeta.id + " = ANY(?);"; // 2

    private static final String UPDATE_FROZEN_SET_FALSE_BY_ORGANIZATION_ID = "UPDATE " +
            ProjectMeta.TABLE_NAME + " " +
            "SET " + ProjectMeta.frozen + " = false " +
            "WHERE " + ProjectMeta.organizationId + " = ? " + // 1
            "AND " + ProjectMeta.frozen + ";";

    @Override
    public void afterPropertiesSet() throws Exception {
        Connection connection = getJdbcTemplate().getDataSource().getConnection();

        connection.prepareStatement(SELECT_PRIVATE_IDS_BY_ORGANIZATION_ID_AND_MIN_MEMBERS_COUNT);
        connection.prepareStatement(SELECT_FROZEN_PRIVATE_IDS_BY_ORGANIZATION_ID_AND_MIN_MEMBERS_COUNT);
        connection.prepareStatement(UPDATE_FROZEN_SET_FALSE_BY_ORGANIZATION_ID);

        connection.close();
    }

    public Long getProjectIdByUiId(String projectUiId) {
        return queryForObjectOrNull(SELECT_PROJECT_ID_BY_UI_ID, Long.class, projectUiId);
    }

    public Long getOrganizationIdById(Long projectId) {
        return queryForObjectOrNull(SELECT_ORGANIZATION_ID_BY_ID, Long.class, projectId);
    }

    public Project getUiIdById(Long projectId) {
        String uiId = queryForObjectOrNull(SELECT_UI_ID_BY_ID, String.class, projectId);
        if (uiId == null) {
            return null;
        }

        Project project = new Project();
        project.setId(projectId);
        project.setUiId(uiId);
        return project;

    }

    public Long getProjectIdByRoadId(Long roadId) {
        return queryForObjectOrNull(SELECT_PROJECT_ID_BY_ROAD_ID, Long.class, roadId);
    }

    public Project getNameAndUiIdByTaskId(Long taskId) {
        return queryForObjectOrNull(SELECT_NAME_AND_UI_ID_BY_TASK_ID, NAME_AND_UI_ID_MAPPER,
                taskId);
    }

    public Project getByIdForDocumentation(Long projectId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_DOCUMENTATION, PROJECT_DOC_MAPPER,
                projectId);
    }

    public Project getByUiIdForDocumentation(String projectUiId) {
        return queryForObjectOrNull(SELECT_BY_UI_ID_FOR_DOCUMENTATION, PROJECT_DOC_MAPPER,
                projectUiId);
    }

    @Deprecated
    public Project getByIdAndFetchDropboxSettings(Long projectId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_DROPBOX_SETTINGS, DROPBOX_PROJECT_MAPPER,
                projectId);
    }

    @Deprecated
    public Project getByUiIdAndFetchDropboxSettings(String projectUiId) {
        return queryForObjectOrNull(SELECT_BY_UI_ID_AND_FETCH_DROPBOX_SETTINGS, DROPBOX_PROJECT_MAPPER,
                projectUiId);
    }

    public Project getByIdWithEverythingForRead(Long projectId) {
        return queryForObjectOrNull(SELECT_BY_ID_WITH_EVERYTHING_FOR_READ, PROJECT_WITH_EVERYTHING_FOR_READ_MAPPER,
                projectId);
    }

    public Project getByIdWithEverythingForReadAndFetchMembershipForSecurity(Long projectId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_WITH_EVERYTHING_FOR_READ_AND_FETCH_MEMBERSHIP_FOR_SECURITY,
                PROJECT_WITH_EVERYTHING_FOR_READ_AND_SECURITY_MEMBERSHIP_MAPPER,
                userId, projectId);
    }

    public List<Long> listPrivateIdsByOrganizationIdAndMinMembersCount(Organization organization, long minMembersCount) {
        return queryForList(SELECT_PRIVATE_IDS_BY_ORGANIZATION_ID_AND_MIN_MEMBERS_COUNT, Long.class,
                organization.getId(), minMembersCount);
    }

    public List<Long> listFrozenPrivateIdsByOrganizationIdAndMinMembersCount(Organization organization, long minMembersCount) {
        return queryForList(SELECT_FROZEN_PRIVATE_IDS_BY_ORGANIZATION_ID_AND_MIN_MEMBERS_COUNT, Long.class,
                organization.getId(), minMembersCount);
    }

    public List<Project> listImportedByOrganizationId(long organizationId) {
        return queryForList(SELECT_BY_ORGANIZATION_ID_INNER_JOIN_IMPORTED_JIRA_PROJECT, PROJECT_NAME_AND_UI_ID_MAPPER,
                organizationId);
    }

    @Deprecated
    public void updateFrozen(Project project) {
        updateSingleRow(UPDATE_FROZEN_BY_ID, project.isFrozen(), project.getId());
    }

    public void freezePrivateByOrganizationIdAndIds(Organization organization, List<Long> projectIds) {
        Array projectIdArray = createArrayOf(JDBCType.BIGINT.getName(), projectIds.toArray());
        getJdbcTemplate().update(UPDATE_FROZEN_SET_TRUE_FOR_PRIVATE_BY_ORGANIZATION_ID_AND_IDS,
                organization.getId(), projectIdArray);
    }

    public void unfreezeByOrganizationId(Organization organization) {
        getJdbcTemplate().update(UPDATE_FROZEN_SET_FALSE_BY_ORGANIZATION_ID, organization.getId());
    }
}
