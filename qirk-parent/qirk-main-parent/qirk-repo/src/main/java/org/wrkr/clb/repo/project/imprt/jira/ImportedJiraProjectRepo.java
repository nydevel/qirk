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
package org.wrkr.clb.repo.project.imprt.jira;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProject;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProjectMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.ImportedJiraProjectMapper;


@Repository
public class ImportedJiraProjectRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + ImportedJiraProjectMeta.TABLE_NAME + " " +
            "(" + ImportedJiraProjectMeta.organizationId + ", " + // 1
            ImportedJiraProjectMeta.projectId + ", " + // 2
            ImportedJiraProjectMeta.uploadTimestamp + ", " + // 3
            ImportedJiraProjectMeta.jiraProjectId + ", " + // 4
            ImportedJiraProjectMeta.jiraProjectKey + ", " + // 5
            ImportedJiraProjectMeta.jiraProjectName + ", " + // 6
            ImportedJiraProjectMeta.updatedAt + ") " + // 7
            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    private static final String SELECT_LAST_UPDATED_AT_BY_ORGANIZATION_ID_AND_PROJECT_ID = "SELECT " +
            "MAX(" + ImportedJiraProjectMeta.updatedAt + ") " +
            "FROM " + ImportedJiraProjectMeta.TABLE_NAME + " " +
            "WHERE " + ImportedJiraProjectMeta.organizationId + " = ? " + // 1
            "AND " + ImportedJiraProjectMeta.projectId + " = ?;"; // 2

    private static final ImportedJiraProjectMapper MAPPER = new ImportedJiraProjectMapper(
            ImportedJiraProjectMeta.TABLE_NAME, ProjectMeta.TABLE_NAME);

    private static final String SELECT_BY_ORGANIZATION_ID = "SELECT " +
            MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ImportedJiraProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + ImportedJiraProjectMeta.TABLE_NAME + "." + ImportedJiraProjectMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + ImportedJiraProjectMeta.TABLE_NAME + "." + ImportedJiraProjectMeta.organizationId + " = ? " + // 1
            "ORDER BY " + ImportedJiraProjectMeta.TABLE_NAME + "." + ImportedJiraProjectMeta.uploadTimestamp + " ASC;";

    public void save(ImportedJiraProject project) {
        getJdbcTemplate().update(INSERT,
                project.getOrganizationId(), project.getProjectId(), project.getUploadTimestamp(),
                project.getJiraProjectId(), project.getJiraProjectKey(), project.getJiraProjectName(),
                Timestamp.from(project.getUpdatedAt().toInstant()));
    }

    public OffsetDateTime getLastUpdatedAtByOrganizationIdAndProjectId(Long organizationId, Long projectId) {
        Timestamp timestamp = queryForObjectOrNull(SELECT_LAST_UPDATED_AT_BY_ORGANIZATION_ID_AND_PROJECT_ID, Timestamp.class,
                organizationId, projectId);
        if (timestamp == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(timestamp.toInstant(), DateTimeUtils.DEFAULT_TIME_ZONE_ID);
    }

    public Map<Long, List<ImportedJiraProject>> mapTimestampToImportedProjectsByOrganizationId(long organizationId) {
        List<Map<String, Object>> results = getJdbcTemplate().queryForList(SELECT_BY_ORGANIZATION_ID, organizationId);

        Map<Long, List<ImportedJiraProject>> map = new HashMap<Long, List<ImportedJiraProject>>();
        List<ImportedJiraProject> projectsAtTimestamp = new ArrayList<ImportedJiraProject>();

        for (Map<String, Object> result : results) {
            Long lastTimestamp = (projectsAtTimestamp.isEmpty() ? null
                    : projectsAtTimestamp.get(projectsAtTimestamp.size() - 1).getUploadTimestamp());
            ImportedJiraProject project = MAPPER.mapRow(result);
            if (project.getUploadTimestamp().equals(lastTimestamp)) {
                projectsAtTimestamp.add(project);
            } else {
                projectsAtTimestamp = new ArrayList<ImportedJiraProject>(Arrays.asList(project));
                map.put(project.getUploadTimestamp(), projectsAtTimestamp);
            }
        }

        return map;
    }
}