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
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMeta;

public class ProjectNameAndUiIdMapper extends BaseMapper<Project> {

    public ProjectNameAndUiIdMapper(String projectTableName) {
        super(projectTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(ProjectMeta.id) + ", " +
                generateSelectColumnStatement(ProjectMeta.uiId) + ", " +
                generateSelectColumnStatement(ProjectMeta.name);
    }

    @Override
    public Project mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong(generateColumnAlias(ProjectMeta.id)));
        project.setName(rs.getString(generateColumnAlias(ProjectMeta.name)));
        project.setUiId(rs.getString(generateColumnAlias(ProjectMeta.uiId)));
        return project;
    }

    public Project mapRow(Map<String, Object> result) {
        Project project = new Project();
        project.setId((Long) result.get(generateColumnAlias(ProjectMeta.id)));
        project.setName((String) result.get(generateColumnAlias(ProjectMeta.name)));
        project.setUiId((String) result.get(generateColumnAlias(ProjectMeta.uiId)));
        return project;
    }
}
