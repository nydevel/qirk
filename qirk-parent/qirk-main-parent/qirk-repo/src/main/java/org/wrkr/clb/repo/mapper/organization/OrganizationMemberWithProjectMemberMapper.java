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
package org.wrkr.clb.repo.mapper.organization;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.repo.mapper.project.ProjectMemberMapper;

public class OrganizationMemberWithProjectMemberMapper extends OrganizationMemberMapper {

    private ProjectMemberMapper projectMemberMapper;

    public OrganizationMemberWithProjectMemberMapper(String orgMemberTableName, String projectMemberTableName) {
        super(orgMemberTableName);
        this.projectMemberMapper = new ProjectMemberMapper(projectMemberTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " + projectMemberMapper.generateSelectColumnsStatement();
    }

    @Override
    public OrganizationMember mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrganizationMember organizationMember = super.mapRow(rs, rowNum);
        if (rs.getObject(projectMemberMapper.generateColumnAlias(ProjectMemberMeta.id)) != null) {
            ProjectMember projectMember = projectMemberMapper.mapRow(rs, rowNum);
            projectMember.setOrganizationMember(organizationMember);
            organizationMember.getProjectMembership().add(projectMember);
        }
        return organizationMember;
    }
}
