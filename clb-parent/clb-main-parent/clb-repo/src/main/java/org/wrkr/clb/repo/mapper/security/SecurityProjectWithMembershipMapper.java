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
package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.repo.mapper.organization.OrganizationMemberWithProjectMemberMapper;

public class SecurityProjectWithMembershipMapper extends SecurityProjectMapper {

    private OrganizationMemberWithProjectMemberMapper organizationMemberMapper;

    public SecurityProjectWithMembershipMapper(String projectTableName,
            String orgMemberTableName, String projectMemberTableName) {
        super(projectTableName);
        this.organizationMemberMapper = new OrganizationMemberWithProjectMemberMapper(orgMemberTableName, projectMemberTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " + organizationMemberMapper.generateSelectColumnsStatement();
    }

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = super.mapRow(rs, rowNum);
        if (rs.getObject(organizationMemberMapper.generateColumnAlias(OrganizationMemberMeta.id)) != null) {
            project.getOrganizationMembers().add(organizationMemberMapper.mapRow(rs, rowNum));
        }
        return project;
    }
}
