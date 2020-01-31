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
import java.util.Map;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;

public class OrganizationMemberMapper extends BaseMapper<OrganizationMember> {

    public OrganizationMemberMapper() {
        super();
    }

    public OrganizationMemberMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(OrganizationMemberMeta.id) + ", " +
                generateSelectColumnStatement(OrganizationMemberMeta.recordVersion) + ", " +
                generateSelectColumnStatement(OrganizationMemberMeta.userId) + ", " +
                generateSelectColumnStatement(OrganizationMemberMeta.organizationId) + ", " +
                generateSelectColumnStatement(OrganizationMemberMeta.enabled) + ", " +
                generateSelectColumnStatement(OrganizationMemberMeta.manager) + ", " +
                generateSelectColumnStatement(OrganizationMemberMeta.fired);
    }

    @Override
    public OrganizationMember mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        OrganizationMember member = new OrganizationMember();

        member.setId(rs.getLong(generateColumnAlias(OrganizationMemberMeta.id)));
        member.setRecordVersion(rs.getLong(generateColumnAlias(OrganizationMemberMeta.recordVersion)));
        member.setUserId(rs.getLong(generateColumnAlias(OrganizationMemberMeta.userId)));
        member.setOrganizationId(rs.getLong(generateColumnAlias(OrganizationMemberMeta.organizationId)));
        member.setEnabled(rs.getBoolean(generateColumnAlias(OrganizationMemberMeta.enabled)));
        member.setManager(rs.getBoolean(generateColumnAlias(OrganizationMemberMeta.manager)));
        member.setFired(rs.getBoolean(generateColumnAlias(OrganizationMemberMeta.fired)));

        return member;
    }

    public OrganizationMember mapRow(Map<String, Object> result) {
        OrganizationMember member = new OrganizationMember();

        member.setId((Long) result.get(generateColumnAlias(OrganizationMemberMeta.id)));
        member.setRecordVersion((Long) result.get(generateColumnAlias(OrganizationMemberMeta.recordVersion)));
        member.setUserId((Long) result.get(generateColumnAlias(OrganizationMemberMeta.userId)));
        member.setOrganizationId((Long) result.get(generateColumnAlias(OrganizationMemberMeta.organizationId)));
        member.setEnabled((boolean) result.get(generateColumnAlias(OrganizationMemberMeta.enabled)));
        member.setManager((boolean) result.get(generateColumnAlias(OrganizationMemberMeta.manager)));
        member.setFired((boolean) result.get(generateColumnAlias(OrganizationMemberMeta.fired)));

        return member;
    }
}
