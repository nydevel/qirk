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

import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.repo.mapper.organization.OrganizationMemberMapper;
import org.wrkr.clb.repo.mapper.security.SecurityProjectWithOrgMapper;

public class ProjectMemberWithOrgMemberAndProjectMapper extends ProjectMemberMapper {

    private OrganizationMemberMapper organizationMemberMapper;
    private SecurityProjectWithOrgMapper projectMapper;

    public ProjectMemberWithOrgMemberAndProjectMapper(String projectMemberTableName, String orgMemberTableName,
            String projectTableName, String orgTableName) {
        super(projectMemberTableName);
        this.organizationMemberMapper = new OrganizationMemberMapper(orgMemberTableName);
        this.projectMapper = new SecurityProjectWithOrgMapper(projectTableName, orgTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                organizationMemberMapper.generateSelectColumnsStatement() + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public ProjectMember mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectMember member = super.mapRow(rs, rowNum);

        member.setOrganizationMember(organizationMemberMapper.mapRow(rs, rowNum));
        member.setProject(projectMapper.mapRow(rs, rowNum));

        return member;
    }
}
