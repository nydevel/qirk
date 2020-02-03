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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.organization.OrganizationMember_;
import org.wrkr.clb.model.organization.Organization_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.JPABaseIdRepo;

@Repository
public class OrganizationRepo extends JPABaseIdRepo<Organization> {

    @Override
    public Organization get(Long id) {
        return get(Organization.class, id);
    }

    public List<Organization> list() {
        return list(Organization.class);
    }

    public Long getOrganizationIdByUiId(String uiId) throws NoResultException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<Organization> root = query.from(Organization.class);

        query.select(root.get(Organization_.ID));
        query.where(cb.equal(root.get(Organization_.uiId), uiId));
        return getSingleResultOrNull(query);
    }

    public Long getOrganizationIdByProjectId(Long projectId) throws NoResultException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<Organization> organizationRoot = query.from(Organization.class);
        Join<Organization, Project> projectJoin = organizationRoot.join(Organization_.projects);

        query.select(organizationRoot.get(Organization_.ID));
        query.where(cb.equal(projectJoin.get(Project_.id), projectId));
        return getSingleResultOrNull(query);
    }

    public boolean existsByUiId(String uiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<Organization> root = query.from(Organization.class);

        query.where(cb.equal(root.get(Organization_.uiId), uiId));
        return exists(root, query);
    }

    public Organization getByUiId(String uiId) {
        return getByUiId(uiId, false);
    }

    public Organization getByUiId(String uiId, boolean fetchOwnerAndDropboxSettingsAndLanguages) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organization> query = cb.createQuery(Organization.class);

        Root<Organization> root = query.from(Organization.class);
        if (fetchOwnerAndDropboxSettingsAndLanguages) {
            root.fetch(Organization_.owner);
            root.fetch(Organization_.dropboxSettings, JoinType.LEFT);
            root.fetch(Organization_.languages, JoinType.LEFT);
            query.distinct(true);
        }

        query.where(cb.equal(root.get(Organization_.uiId), uiId));
        return getSingleResultOrNull(query);
    }

    public Organization getAndFetchDropboxSettings(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organization> query = cb.createQuery(Organization.class);

        Root<Organization> root = query.from(Organization.class);
        root.fetch(Organization_.dropboxSettings, JoinType.LEFT);

        query = query.where(cb.equal(root.get(Organization_.id), id));
        return getSingleResultOrNull(query);
    }

    public Organization getAndFetchOwnerAndDropboxSettingsAndLanguages(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organization> query = cb.createQuery(Organization.class);

        Root<Organization> root = query.from(Organization.class);
        root.fetch(Organization_.owner);
        root.fetch(Organization_.dropboxSettings, JoinType.LEFT);
        root.fetch(Organization_.languages, JoinType.LEFT);

        query = query.where(cb.equal(root.get(Organization_.id), id));
        query.distinct(true);
        return getSingleResultOrNull(query);
    }

    public List<Long> listOrganizationIdsMyMemberUser(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> idsQuery = cb.createQuery(Long.class);

        Root<Organization> organizationRoot = idsQuery.from(Organization.class);
        Join<Organization, OrganizationMember> memberJoin = organizationRoot.join(Organization_.members);

        idsQuery.select(organizationRoot.get(Organization_.ID));
        idsQuery.where(cb.equal(memberJoin.get(OrganizationMember_.user), user),
                cb.equal(memberJoin.get(OrganizationMember_.fired), false));
        return getResultList(idsQuery);
    }

    public List<Organization> listByNotFiredMemberUserAndFetchOwner(User user, boolean filterByManager) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organization> query = cb.createQuery(Organization.class);

        Root<Organization> organizationRoot = query.from(Organization.class);
        organizationRoot.fetch(Organization_.owner);
        Join<Organization, OrganizationMember> organizationMemberJoin = organizationRoot.join(Organization_.members);

        List<Predicate> predicates = new ArrayList<Predicate>(Arrays.asList(
                cb.equal(organizationMemberJoin.get(OrganizationMember_.user), user),
                cb.equal(organizationMemberJoin.get(OrganizationMember_.enabled), true),
                cb.equal(organizationMemberJoin.get(OrganizationMember_.fired), false)));
        if (filterByManager) {
            predicates.add(cb.equal(organizationMemberJoin.get(OrganizationMember_.manager), true));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<Order> orderings = new ArrayList<Order>();
        if (!filterByManager) {
            orderings.add(cb.desc(organizationMemberJoin.get(OrganizationMember_.manager)));
        }
        orderings.add(cb.asc(organizationRoot.get(Organization_.name)));
        query.orderBy(orderings.toArray(new Order[0]));

        return getResultList(query);
    }
}
