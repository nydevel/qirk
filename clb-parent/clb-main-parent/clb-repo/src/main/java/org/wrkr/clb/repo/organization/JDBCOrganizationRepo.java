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
package org.wrkr.clb.repo.organization;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.organization.OrganizationMeta;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.organization.OrganizationMapper;
import org.wrkr.clb.repo.mapper.organization.ShortOrganizationMapper;


@Repository
public class JDBCOrganizationRepo extends JDBCBaseMainRepo implements InitializingBean {

    private static final String SELECT_1_BY_ID = "SELECT 1 FROM " + OrganizationMeta.TABLE_NAME + " " +
            "WHERE " + OrganizationMeta.id + " = ?;"; // 1

    private static final OrganizationMapper ORGANIZATION_MAPPER = new OrganizationMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            ORGANIZATION_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " " +
            "WHERE " + OrganizationMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_UI_ID = "SELECT " +
            ORGANIZATION_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " " +
            "WHERE " + OrganizationMeta.uiId + " = ?;"; // 1

    private static final String SELECT_OWNER_IDS = "SELECT " + OrganizationMeta.ownerId + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " ";

    private static final ShortOrganizationMapper SHORT_ORGANIZATION_MAPPER = new ShortOrganizationMapper(
            OrganizationMeta.TABLE_NAME);

    private static final String SELECT_BY_OWNER_ID = "SELECT " +
            SHORT_ORGANIZATION_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMemberMeta.TABLE_NAME + " " +
            "ON (" + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " = " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " " +
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + ") " +
            "LEFT JOIN " + ProjectMemberMeta.TABLE_NAME + " " +
            "ON (" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.organizationMemberId + " " +
            "AND NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + ") " +
            "LEFT JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.ownerId + " = ? " + // 1
            "AND (" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.manager + " " +
            "OR " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.isPrivate + ") " +
            "GROUP BY " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + ";";

    private static final String UPDATE_FROZEN_BY_ID = "UPDATE " + OrganizationMeta.TABLE_NAME + " " +
            "SET " + OrganizationMeta.frozen + " = ? " +
            "WHERE " + OrganizationMeta.id + " = ?;";

    @Override
    public void afterPropertiesSet() throws Exception {
        Connection connection = getJdbcTemplate().getDataSource().getConnection();

        connection.prepareStatement(SELECT_OWNER_IDS);
        connection.prepareStatement(SELECT_BY_OWNER_ID);

        connection.close();
    }

    public boolean exists(Long organizationId) {
        return exists(SELECT_1_BY_ID, organizationId);
    }

    public Organization getById(Long organizationId) {
        return queryForObjectOrNull(SELECT_BY_ID, ORGANIZATION_MAPPER, organizationId);
    }

    public Organization getByUiId(String organizationUiId) {
        return queryForObjectOrNull(SELECT_BY_UI_ID, ORGANIZATION_MAPPER, organizationUiId);
    }

    public Set<Long> listOwnerIds() {
        return new HashSet<Long>(queryForList(SELECT_OWNER_IDS, Long.class));
    }

    public List<Organization> listByOwnerId(Long ownerId) {
        return queryForList(SELECT_BY_OWNER_ID, SHORT_ORGANIZATION_MAPPER,
                ownerId);
    }

    @Deprecated
    public void updateFrozen(Organization organization) {
        updateSingleRow(UPDATE_FROZEN_BY_ID, organization.isFrozen(), organization.getId());
    }
}
