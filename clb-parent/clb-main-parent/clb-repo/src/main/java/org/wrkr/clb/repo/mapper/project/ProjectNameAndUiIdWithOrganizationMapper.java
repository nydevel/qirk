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

import org.wrkr.clb.model.BaseUiIdEntityMeta;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.repo.mapper.UiIdMapper;

public class ProjectNameAndUiIdWithOrganizationMapper extends ProjectNameAndUiIdMapper {

    private UiIdMapper<Organization> organizationMapper;

    public ProjectNameAndUiIdWithOrganizationMapper(String projectTableName, String orgTableName) {
        super(projectTableName);
        organizationMapper = new UiIdMapper<Organization>(orgTableName) {

            @Override
            public Organization mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
                Organization organization = new Organization();
                organization.setId(rs.getLong(generateColumnAlias(BaseUiIdEntityMeta.id)));
                organization.setUiId(rs.getString(generateColumnAlias(BaseUiIdEntityMeta.uiId)));
                return organization;
            }
        };
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                organizationMapper.generateSelectColumnsStatement();
    }

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = super.mapRow(rs, rowNum);
        project.setOrganization(organizationMapper.mapRow(rs, rowNum));
        return project;
    }
}
