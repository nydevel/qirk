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

import java.sql.Array;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.organization.OrganizationMemberMeta;
import org.wrkr.clb.model.organization.OrganizationMeta;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.organization.OrganizationMemberMapper;
import org.wrkr.clb.repo.mapper.organization.OrganizationMemberWithOrgAndOtherMemberMapper;
import org.wrkr.clb.repo.mapper.organization.OrganizationMemberWithOrgMapper;
import org.wrkr.clb.repo.mapper.organization.ShortOrganizationMemberWithUserAndEmailMapper;
import org.wrkr.clb.repo.mapper.organization.ShortOrganizationMemberWithUserMapper;

@Repository
public class JDBCOrganizationMemberRepo extends JDBCBaseMainRepo {

    private static final OrganizationMemberWithOrgMapper MEMBER_WITH_ORG_MAPPER = new OrganizationMemberWithOrgMapper(
            OrganizationMemberMeta.TABLE_NAME, OrganizationMeta.TABLE_NAME);

    private static final String SELECT_NOT_FIRED_BY_ID_AND_FETCH_ORGANIZATION = "SELECT " +
            MEMBER_WITH_ORG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " = ? " + // 1
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + ";";

    private static final OrganizationMemberMapper ORGANIZATION_MEMBER_MAPPER = new OrganizationMemberMapper();

    private static final String SELECT_NOT_FIRED_BY_USER_ID_AND_ORGANIZATION_ID = "SELECT " +
            ORGANIZATION_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "WHERE " + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND " + OrganizationMemberMeta.organizationId + " = ? " + // 2
            "AND NOT " + OrganizationMemberMeta.fired + ";";

    private static final OrganizationMemberMapper TABLENAME_ORGANIZATION_MEMBER_MAPPER = new OrganizationMemberMapper(
            OrganizationMemberMeta.TABLE_NAME);

    private static final String SELECT_NOT_FIRED_BY_USER_ID_AND_ORGANIZATION_UI_ID = "SELECT " +
            TABLENAME_ORGANIZATION_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.uiId + " = ? " + // 2
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + ";";

    private static final String OTHER_ORG_MEMBER_TABLE_ALIAS = "other_user_org";
    private static final OrganizationMemberWithOrgAndOtherMemberMapper ORGANIZATION_MEMBER_WITH_ORG_AND_OTHER_MEMBER_MAPPER = new OrganizationMemberWithOrgAndOtherMemberMapper(
            OrganizationMemberMeta.TABLE_NAME, OrganizationMeta.TABLE_NAME, OTHER_ORG_MEMBER_TABLE_ALIAS);
    private static final String SELECT_NOT_FIRED_BY_ID_AND_FETCH_ORG_AND_OTHER_MEMBER_BY_USER_ID = "SELECT " +
            ORGANIZATION_MEMBER_WITH_ORG_AND_OTHER_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "LEFT JOIN " + OrganizationMemberMeta.TABLE_NAME + " AS " + OTHER_ORG_MEMBER_TABLE_ALIAS + " " +
            "ON " + OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " = " +
            OTHER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.organizationId + " " +
            "AND " + OTHER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND NOT " + OTHER_ORG_MEMBER_TABLE_ALIAS + "." + OrganizationMemberMeta.fired + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " = ? " + // 2
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + ";";

    private static final String SELECT_ID_BY_USER_ID_AND_PROJECT_ID = "SELECT " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 2

    private static final String SELECT_ID_BY_USER_ID_AND_PROJECT_UI_ID = "SELECT " +
            OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.organizationId + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ?;"; // 2

    private static final String SELECT_NOT_FIRED_BY_ORGANIZATION_ID_AND_USER_IDS_PREFIX = "SELECT " +
            ORGANIZATION_MEMBER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = ? " + // 1
            "AND " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " IN ("; // 2

    private static final String SELECT_NOT_FIRED_BY_USER_ID_AND_FETCH_ORGANIZATION = "SELECT " +
            MEMBER_WITH_ORG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + OrganizationMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = " +
            OrganizationMeta.TABLE_NAME + "." + OrganizationMeta.id + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = ? " + // 1
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + ";";

    private static final ShortOrganizationMemberWithUserAndEmailMapper MEMBER_WITH_USER_AND_EMAIL_MAPPER = new ShortOrganizationMemberWithUserAndEmailMapper(
            OrganizationMemberMeta.TABLE_NAME, UserMeta.TABLE_NAME);

    private static final String SELECT_NOT_FIRED_BY_ORGANIZATION_ID_AND_FETCH_USER_PREFIX = "SELECT " +
            MEMBER_WITH_USER_AND_EMAIL_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = ? " + // 1
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + ";";

    private static final ShortOrganizationMemberWithUserMapper MEMBER_WITH_USER_MAPPER = new ShortOrganizationMemberWithUserMapper(
            OrganizationMemberMeta.TABLE_NAME, UserMeta.TABLE_NAME);

    private static final String SELECT_NOT_FIRED_BY_ORGANIZATION_ID_AND_MEMBER_IDS_AND_FETCH_USER_PREFIX = "SELECT " +
            MEMBER_WITH_USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "INNER JOIN " + UserMeta.TABLE_NAME + " " +
            "ON " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.userId + " = " +
            UserMeta.TABLE_NAME + "." + UserMeta.id + " " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = ? " + // 1
            "AND " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " IN ("; // 2

    private static final String COUNT_NOT_FIRED_MANAGERS_OR_PROJECT_MEMBERS_BY_ORGANIZATION_ID_AND_PROJECT_IDS = "SELECT " +
            "COUNT(DISTINCT(" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + ")) " +
            "FROM " + OrganizationMemberMeta.TABLE_NAME + " " +
            "LEFT JOIN " + ProjectMemberMeta.TABLE_NAME + " " +
            "ON (" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.id + " = " +
            ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.organizationMemberId + " " +
            "AND NOT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.fired + ") " +
            "WHERE " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.organizationId + " = ? " + // 1
            "AND NOT " + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.fired + " " +
            "AND (" + OrganizationMemberMeta.TABLE_NAME + "." + OrganizationMemberMeta.manager + " " +
            "OR " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.projectId + " = ANY(?));"; // 2

    public OrganizationMember getNotFiredByIdAndFetchOrganization(Long memberId) {
        return queryForObjectOrNull(SELECT_NOT_FIRED_BY_ID_AND_FETCH_ORGANIZATION,
                MEMBER_WITH_ORG_MAPPER,
                memberId);
    }

    public OrganizationMember getNotFiredByUserIdAndOrganizationId(Long userId, Long organizationId) {
        return queryForObjectOrNull(SELECT_NOT_FIRED_BY_USER_ID_AND_ORGANIZATION_ID, ORGANIZATION_MEMBER_MAPPER,
                userId, organizationId);
    }

    public OrganizationMember getNotFiredByUserIdAndOrganizationUiId(Long userId, String organizationUiId) {
        return queryForObjectOrNull(SELECT_NOT_FIRED_BY_USER_ID_AND_ORGANIZATION_UI_ID, TABLENAME_ORGANIZATION_MEMBER_MAPPER,
                userId, organizationUiId);
    }

    public OrganizationMember getNotFiredByIdAndFetchOrganizationAndOtherMemberByUserId(Long memberId, Long userId) {
        OrganizationMember member = queryForObjectOrNull(SELECT_NOT_FIRED_BY_ID_AND_FETCH_ORG_AND_OTHER_MEMBER_BY_USER_ID,
                ORGANIZATION_MEMBER_WITH_ORG_AND_OTHER_MEMBER_MAPPER,
                userId, memberId);
        int otherMembersSize = member.getOrganization().getMembers().size();
        if (otherMembersSize > 1) {
            throw new IncorrectResultSizeDataAccessException(1, otherMembersSize);
        }
        return member;
    }

    public Long getIdByUserIdAndProjectId(Long userId, Long projectId) {
        return queryForObjectOrNull(SELECT_ID_BY_USER_ID_AND_PROJECT_ID, Long.class,
                userId, projectId);
    }

    public Long getIdByUserIdAndProjectUiId(Long userId, String projectUiId) {
        return queryForObjectOrNull(SELECT_ID_BY_USER_ID_AND_PROJECT_UI_ID, Long.class,
                userId, projectUiId);
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<OrganizationMember> listNotFiredByOrganizationIdAndUserIds(
            Long organizationId, Collection<Long> userIds) {
        if (userIds.isEmpty()) {
            return new ArrayList<OrganizationMember>();
        }
        return queryForList(
                insertNBindValues(SELECT_NOT_FIRED_BY_ORGANIZATION_ID_AND_USER_IDS_PREFIX, userIds.size(), ");"),
                ArrayUtils.addAll(new Object[] { organizationId }, userIds.toArray()),
                ORGANIZATION_MEMBER_MAPPER);
    }

    public List<OrganizationMember> listNotFiredByUserIdAndFetchOrganization(Long userId) {
        return queryForList(SELECT_NOT_FIRED_BY_USER_ID_AND_FETCH_ORGANIZATION, MEMBER_WITH_ORG_MAPPER, userId);
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<OrganizationMember> listNotFiredByOrganizationIdAndFetchUser(Long organizationId) {
        return queryForList(SELECT_NOT_FIRED_BY_ORGANIZATION_ID_AND_FETCH_USER_PREFIX, MEMBER_WITH_USER_AND_EMAIL_MAPPER,
                organizationId);
    }

    public List<OrganizationMember> listNotFiredByOrganizationIdAndMemberIdsAndFetchUser(
            Long organizationId, Collection<Long> memberIds) {
        return queryForList(
                insertNBindValues(
                        SELECT_NOT_FIRED_BY_ORGANIZATION_ID_AND_MEMBER_IDS_AND_FETCH_USER_PREFIX, memberIds.size(), ");"),
                ArrayUtils.addAll(new Object[] { organizationId }, memberIds.toArray()),
                MEMBER_WITH_USER_MAPPER);
    }

    public long countNotFiredManagersOrProjectMembersByOrganizationIdAndProjectId(
            Organization organization, Project project) {
        return countNotFiredManagersOrProjectMembersByOrganizationIdAndProjectIds(organization, Arrays.asList(project.getId()));
    }

    public long countNotFiredManagersOrProjectMembersByOrganizationIdAndProjectIds(
            Organization organization, List<Long> projectIds) {
        Array projectIdArray = createArrayOf(JDBCType.BIGINT.getName(), projectIds.toArray());
        Long result = queryForObjectOrNull(COUNT_NOT_FIRED_MANAGERS_OR_PROJECT_MEMBERS_BY_ORGANIZATION_ID_AND_PROJECT_IDS,
                Long.class, organization.getId(), projectIdArray);
        return (result == null ? 0L : result);
    }
}
