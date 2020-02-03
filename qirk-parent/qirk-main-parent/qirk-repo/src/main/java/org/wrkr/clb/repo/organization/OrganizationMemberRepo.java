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

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.organization.OrganizationMember_;
import org.wrkr.clb.model.organization.Organization_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.Task_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.JPABaseIdRepo;

@Repository
public class OrganizationMemberRepo extends JPABaseIdRepo<OrganizationMember> {

    /**
     * @deprecated use {@link #getNotFired()} instead
     */
    @Deprecated
    @Override
    public OrganizationMember get(Long id) {
        return get(OrganizationMember.class, id);
    }

    public boolean existsNotFiredEnabledManagersByOrganization(Organization organization) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<OrganizationMember> organizationMemberRoot = query.from(OrganizationMember.class);

        query.where(cb.equal(organizationMemberRoot.get(OrganizationMember_.manager), true),
                cb.equal(organizationMemberRoot.get(OrganizationMember_.enabled), true),
                cb.equal(organizationMemberRoot.get(OrganizationMember_.fired), false),
                cb.equal(organizationMemberRoot.get(OrganizationMember_.organization), organization));
        return exists(organizationMemberRoot, query);
    }

    public Long getOrganizationMemberIdByUserAndProjectId(User user, Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<OrganizationMember> organizationMemberRoot = query.from(OrganizationMember.class);
        Join<OrganizationMember, Organization> organizationJoin = organizationMemberRoot.join(OrganizationMember_.organization);
        Join<Organization, Project> projectJoin = organizationJoin.join(Organization_.projects);

        query.select(organizationMemberRoot.get(OrganizationMember_.ID));
        query.where(cb.equal(organizationMemberRoot.get(OrganizationMember_.user), user),
                cb.equal(projectJoin.get(Project_.id), projectId));
        return getSingleResultOrNull(query);
    }

    public Long getOrganizationMemberIdByUserAndProjectUiId(User user, String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<OrganizationMember> organizationMemberRoot = query.from(OrganizationMember.class);
        Join<OrganizationMember, Organization> organizationJoin = organizationMemberRoot.join(OrganizationMember_.organization);
        Join<Organization, Project> projectJoin = organizationJoin.join(Organization_.projects);

        query.select(organizationMemberRoot.get(OrganizationMember_.ID));
        query.where(cb.equal(organizationMemberRoot.get(OrganizationMember_.user), user),
                cb.equal(projectJoin.get(Project_.uiId), projectUiId));
        return getSingleResultOrNull(query);
    }

    public OrganizationMember getNotFired(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> root = query.from(OrganizationMember.class);

        query.where(cb.equal(root.get(OrganizationMember_.id), id), cb.equal(root.get(OrganizationMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public OrganizationMember getNotFiredAndFetchUser(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> root = query.from(OrganizationMember.class);
        root.fetch(OrganizationMember_.user);

        query.where(cb.equal(root.get(OrganizationMember_.id), id), cb.equal(root.get(OrganizationMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public OrganizationMember getNotFiredAndFetchUserAndOrganization(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> root = query.from(OrganizationMember.class);
        root.fetch(OrganizationMember_.user);
        root.fetch(OrganizationMember_.organization);

        query.where(cb.equal(root.get(OrganizationMember_.id), id), cb.equal(root.get(OrganizationMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public OrganizationMember getNotFiredByIdAndOrganizationAndFetchUser(Long id, Organization organization) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> root = query.from(OrganizationMember.class);
        root.fetch(OrganizationMember_.user);

        query.where(cb.equal(root.get(OrganizationMember_.id), id),
                cb.equal(root.get(OrganizationMember_.fired), false),
                cb.equal(root.get(OrganizationMember_.organization), organization));
        return getSingleResultOrNull(query);
    }

    public OrganizationMember getByUserAndOrganization(User user, Organization organization) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> root = query.from(OrganizationMember.class);

        query.where(cb.equal(root.get(OrganizationMember_.user), user),
                cb.equal(root.get(OrganizationMember_.organization), organization));
        return getSingleResultOrNull(query);
    }

    private OrganizationMember getSingleResultOrNullAndSetUser(CriteriaQuery<OrganizationMember> query, User user) {
        OrganizationMember result = getSingleResultOrNull(query);
        if (result != null) {
            result.setUser(user);
        }
        return result;
    }

    public OrganizationMember getNotFiredByUserAndOrganization(User user, Organization organization) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> root = query.from(OrganizationMember.class);

        query.where(cb.equal(root.get(OrganizationMember_.user), user),
                cb.equal(root.get(OrganizationMember_.organization), organization),
                cb.equal(root.get(OrganizationMember_.fired), false));
        return getSingleResultOrNullAndSetUser(query, user);
    }

    public OrganizationMember getNotFiredByUserAndOrganizationId(User user, Long organizationId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> organizationMemberRoot = query.from(OrganizationMember.class);
        Join<OrganizationMember, Organization> organizationJoin = organizationMemberRoot.join(OrganizationMember_.organization);

        query.where(cb.equal(organizationMemberRoot.get(OrganizationMember_.user), user),
                cb.equal(organizationJoin.get(Organization_.id), organizationId),
                cb.equal(organizationMemberRoot.get(OrganizationMember_.fired), false));
        return getSingleResultOrNullAndSetUser(query, user);
    }

    public OrganizationMember getNotFiredByUserAndOrganizationUiId(User user, String organizationUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> organizationMemberRoot = query.from(OrganizationMember.class);
        Join<OrganizationMember, Organization> organizationJoin = organizationMemberRoot.join(OrganizationMember_.organization);

        query.where(cb.equal(organizationMemberRoot.get(OrganizationMember_.user), user),
                cb.equal(organizationJoin.get(Organization_.uiId), organizationUiId),
                cb.equal(organizationMemberRoot.get(OrganizationMember_.fired), false));
        return getSingleResultOrNullAndSetUser(query, user);
    }

    public OrganizationMember getNotFiredByUserAndProjectId(User user, Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> organizationMemberRoot = query.from(OrganizationMember.class);
        Join<OrganizationMember, Organization> organizationJoin = organizationMemberRoot.join(OrganizationMember_.organization);
        Join<Organization, Project> projectJoin = organizationJoin.join(Organization_.projects);

        query.where(cb.equal(organizationMemberRoot.get(OrganizationMember_.user), user),
                cb.equal(projectJoin.get(Project_.id), projectId),
                cb.equal(organizationMemberRoot.get(OrganizationMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public OrganizationMember getNotFiredByUserAndProjectUiId(User user, String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> organizationMemberRoot = query.from(OrganizationMember.class);
        Join<OrganizationMember, Organization> organizationJoin = organizationMemberRoot.join(OrganizationMember_.organization);
        Join<Organization, Project> projectJoin = organizationJoin.join(Organization_.projects);

        query.where(cb.equal(organizationMemberRoot.get(OrganizationMember_.user), user),
                cb.equal(projectJoin.get(Project_.uiId), projectUiId),
                cb.equal(organizationMemberRoot.get(OrganizationMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public OrganizationMember getNotFiredByUserAndTaskId(User user, Long taskId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> organizationMemberRoot = query.from(OrganizationMember.class);
        Join<OrganizationMember, Organization> organizationJoin = organizationMemberRoot.join(OrganizationMember_.organization);
        Join<Organization, Project> projectJoin = organizationJoin.join(Organization_.projects);
        Join<Project, Task> taskJoin = projectJoin.join(Project_.tasks);

        query.where(cb.equal(organizationMemberRoot.get(OrganizationMember_.user), user),
                cb.equal(taskJoin.get(Task_.id), taskId),
                cb.equal(organizationMemberRoot.get(OrganizationMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public List<OrganizationMember> listNotFiredByIdsAndOrganization(List<Long> ids, Organization organization) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> root = query.from(OrganizationMember.class);

        query.where(root.get(OrganizationMember_.id).in(ids), cb.equal(root.get(OrganizationMember_.fired), false),
                cb.equal(root.get(OrganizationMember_.organization), organization));
        return getResultList(query);
    }

    public List<OrganizationMember> listNotFiredByOrganizationIdAndFetchUser(Long organizationId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMember> query = cb.createQuery(OrganizationMember.class);

        Root<OrganizationMember> memberRoot = query.from(OrganizationMember.class);
        memberRoot.fetch(OrganizationMember_.user);
        Join<OrganizationMember, Organization> organizationJoin = memberRoot.join(OrganizationMember_.organization);

        query.where(cb.equal(organizationJoin.get(Organization_.id), organizationId),
                cb.equal(memberRoot.get(OrganizationMember_.fired), false));
        query.orderBy(cb.desc(memberRoot.get(OrganizationMember_.id)));
        return getResultList(query);
    }
}
