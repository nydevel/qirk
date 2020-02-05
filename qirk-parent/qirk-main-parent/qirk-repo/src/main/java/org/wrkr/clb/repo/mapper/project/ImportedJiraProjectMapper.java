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
package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProject;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProjectMeta;

public class ImportedJiraProjectMapper extends BaseMapper<ImportedJiraProject> {

    private static final String TABLE_NAME_SHORT = "ijp";

    private ProjectNameAndUiIdMapper projectMapper;

    public ImportedJiraProjectMapper(String importedProjectTableName, String projectTableName) {
        super(importedProjectTableName);
        projectMapper = new ProjectNameAndUiIdMapper(projectTableName);
    }

    @Override
    public String generateColumnAlias(String columnLabel) {
        return (tableAlias.isEmpty() ? columnLabel : TABLE_NAME_SHORT + "__" + columnLabel);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(ImportedJiraProjectMeta.projectId) + ", " +
                generateSelectColumnStatement(ImportedJiraProjectMeta.uploadTimestamp) + ", " +
                generateSelectColumnStatement(ImportedJiraProjectMeta.jiraProjectId) + ", " +
                generateSelectColumnStatement(ImportedJiraProjectMeta.jiraProjectKey) + ", " +
                generateSelectColumnStatement(ImportedJiraProjectMeta.jiraProjectName) + ", " +
                generateSelectColumnStatement(ImportedJiraProjectMeta.updatedAt) + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public ImportedJiraProject mapRow(ResultSet rs, int rowNum) throws SQLException {
        ImportedJiraProject project = new ImportedJiraProject();

        project.setProjectId(rs.getLong(generateColumnAlias(ImportedJiraProjectMeta.projectId)));
        project.setUploadTimestamp(rs.getLong(generateColumnAlias(ImportedJiraProjectMeta.uploadTimestamp)));
        project.setJiraProjectId(rs.getLong(generateColumnAlias(ImportedJiraProjectMeta.jiraProjectId)));
        project.setJiraProjectKey(rs.getString(generateColumnAlias(ImportedJiraProjectMeta.jiraProjectKey)));
        project.setJiraProjectName(rs.getString(generateColumnAlias(ImportedJiraProjectMeta.jiraProjectName)));
        project.setUpdatedAt(getOffsetDateTime(rs, generateColumnAlias(ImportedJiraProjectMeta.updatedAt)));

        project.setProject(projectMapper.mapRow(rs, rowNum));

        return project;
    }

    public ImportedJiraProject mapRow(Map<String, Object> result) {
        ImportedJiraProject project = new ImportedJiraProject();

        project.setProjectId((Long) result.get(generateColumnAlias(ImportedJiraProjectMeta.projectId)));
        project.setUploadTimestamp((Long) result.get(generateColumnAlias(ImportedJiraProjectMeta.uploadTimestamp)));
        project.setJiraProjectId((Long) result.get(generateColumnAlias(ImportedJiraProjectMeta.jiraProjectId)));
        project.setJiraProjectKey((String) result.get(generateColumnAlias(ImportedJiraProjectMeta.jiraProjectKey)));
        project.setJiraProjectName((String) result.get(generateColumnAlias(ImportedJiraProjectMeta.jiraProjectName)));
        project.setUpdatedAt(getOffsetDateTime(result, generateColumnAlias(ImportedJiraProjectMeta.updatedAt)));

        project.setProject(projectMapper.mapRow(result));

        return project;
    }
}
