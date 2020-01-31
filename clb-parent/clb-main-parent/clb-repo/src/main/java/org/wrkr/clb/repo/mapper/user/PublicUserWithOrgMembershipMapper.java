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
package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.mapper.organization.OrganizationMemberMapper;

public class PublicUserWithOrgMembershipMapper extends PublicUserMapper {

    private OrganizationMemberMapper orgMemberMapper;

    public PublicUserWithOrgMembershipMapper(String userTableName, String orgMemberTableName) {
        super(userTableName);
        orgMemberMapper = new OrganizationMemberMapper(orgMemberTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                orgMemberMapper.generateSelectColumnsStatement();
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);
        if (rs.getObject(orgMemberMapper.generateColumnAlias(OrganizationMemberMeta.id)) != null) {
            user.getOrganizationMembership().add(orgMemberMapper.mapRow(rs, rowNum));
        }
        return user;
    }

    @Override
    public User mapRow(Map<String, Object> result) {
        User user = super.mapRow(result);
        if (result.get(orgMemberMapper.generateColumnAlias(OrganizationMemberMeta.id)) != null) {
            user.getOrganizationMembership().add(orgMemberMapper.mapRow(result));
        }
        return user;
    }

    public OrganizationMember mapRowForOrgMember(Map<String, Object> result) {
        return orgMemberMapper.mapRow(result);
    }
}