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

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMeta;

public class OrganizationMapper extends BaseMapper<Organization> {

    public OrganizationMapper() {
        super();
    }

    public OrganizationMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(OrganizationMeta.id) + ", " +
                generateSelectColumnStatement(OrganizationMeta.recordVersion) + ", " +
                generateSelectColumnStatement(OrganizationMeta.name) + ", " +
                generateSelectColumnStatement(OrganizationMeta.uiId) + ", " +
                generateSelectColumnStatement(OrganizationMeta.isPrivate) + ", " +
                generateSelectColumnStatement(OrganizationMeta.ownerId) + ", " +
                generateSelectColumnStatement(OrganizationMeta.predefinedForUser) + ", " +
                generateSelectColumnStatement(OrganizationMeta.frozen) + ", " +
                generateSelectColumnStatement(OrganizationMeta.dropboxSettingsId);
    }

    @Override
    public Organization mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Organization organization = new Organization();

        organization.setId(rs.getLong(generateColumnAlias(OrganizationMeta.id)));
        organization.setRecordVersion(rs.getLong(generateColumnAlias(OrganizationMeta.recordVersion)));
        organization.setName(rs.getString(generateColumnAlias(OrganizationMeta.name)));
        organization.setUiId(rs.getString(generateColumnAlias(OrganizationMeta.uiId)));
        organization.setPrivate(rs.getBoolean(generateColumnAlias(OrganizationMeta.isPrivate)));
        organization.setOwnerId(rs.getLong(generateColumnAlias(OrganizationMeta.ownerId)));
        organization.setPredefinedForUser(rs.getBoolean(generateColumnAlias(OrganizationMeta.predefinedForUser)));
        organization.setFrozen(rs.getBoolean(generateColumnAlias(OrganizationMeta.frozen)));
        organization.setDropboxSettingsId((Long) rs.getObject(generateColumnAlias(OrganizationMeta.dropboxSettingsId))); // nullable

        return organization;
    }
}
