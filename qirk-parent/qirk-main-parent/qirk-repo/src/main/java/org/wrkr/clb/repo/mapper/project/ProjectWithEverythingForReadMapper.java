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
package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMeta;

public class ProjectWithEverythingForReadMapper extends ProjectNameAndUiIdMapper {

    public ProjectWithEverythingForReadMapper(String projectTableName) {
        super(projectTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(ProjectMeta.recordVersion) + ", " +
                generateSelectColumnStatement(ProjectMeta.key) + ", " +
                generateSelectColumnStatement(ProjectMeta.isPrivate) + ", " +
                generateSelectColumnStatement(ProjectMeta.descriptionHtml) + ", " +
                generateSelectColumnStatement(ProjectMeta.descriptionMd) + ", " +
                generateSelectColumnStatement(ProjectMeta.documentationHtml) + ", " +
                generateSelectColumnStatement(ProjectMeta.documentationMd);
    }

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = super.mapRow(rs, rowNum);

        project.setRecordVersion(rs.getLong(generateColumnAlias(ProjectMeta.recordVersion)));
        project.setKey(rs.getString(generateColumnAlias(ProjectMeta.key)));
        project.setPrivate(rs.getBoolean(generateColumnAlias(ProjectMeta.isPrivate)));
        project.setDescriptionHtml(rs.getString(generateColumnAlias(ProjectMeta.descriptionHtml)));
        project.setDescriptionMd(rs.getString(generateColumnAlias(ProjectMeta.descriptionMd)));
        project.setDocumentationHtml(rs.getString(generateColumnAlias(ProjectMeta.documentationHtml)));
        project.setDocumentationMd(rs.getString(generateColumnAlias(ProjectMeta.documentationMd)));

        return project;
    }
}
