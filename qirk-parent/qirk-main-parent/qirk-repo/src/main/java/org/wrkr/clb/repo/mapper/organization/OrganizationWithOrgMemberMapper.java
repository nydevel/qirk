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

import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;

public class OrganizationWithOrgMemberMapper extends OrganizationMapper {

    private OrganizationMemberMapper memberMapper;

    public OrganizationWithOrgMemberMapper(String organizationTableName, String memberTableName) {
        super(organizationTableName);
        this.memberMapper = new OrganizationMemberMapper(memberTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " + memberMapper.generateSelectColumnsStatement();
    }

    @Override
    public Organization mapRow(ResultSet rs, int rowNum) throws SQLException {
        Organization organization = super.mapRow(rs, rowNum);
        if (rs.getObject(memberMapper.generateColumnAlias(OrganizationMemberMeta.id)) != null) {
            OrganizationMember member = memberMapper.mapRow(rs, rowNum);
            member.setOrganization(organization);
            organization.getMembers().add(member);
        }
        return organization;
    }
}
