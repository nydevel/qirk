/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
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
package org.wrkr.clb.repo.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMember_;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.Task_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.User_;
import org.wrkr.clb.repo.JPABaseIdRepo;

@Repository
public class ProjectRepo extends JPABaseIdRepo<Project> {

    @Override
    public Project get(Long id) {
        return get(Project.class, id);
    }

    public List<Project> listByIds(List<Long> ids) {
        return listByIds(Project.class, ids);
    }

    public boolean existsByUiId(String uiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<Project> root = query.from(Project.class);

        query.where(cb.equal(root.get(Project_.uiId), uiId));
        return exists(root, query);
    }

    public Project getAndFetchTaskNumberSequence(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> root = query.from(Project.class);
        root.fetch(Project_.taskNumberSequence);

        query.where(cb.equal(root.get(Project_.id), id));
        return getSingleResultOrNull(query);
    }

    public Project getByUiId(String uiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> root = query.from(Project.class);

        query.where(cb.equal(root.get(Project_.uiId), uiId));
        return getSingleResultOrNull(query);
    }

    public Project getByUiIdAndFetchTaskNumberSequence(String uiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> root = query.from(Project.class);
        root.fetch(Project_.taskNumberSequence);

        query.where(cb.equal(root.get(Project_.uiId), uiId));
        return getSingleResultOrNull(query);
    }

    public Project getByTaskId(Long taskId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> projectRoot = query.from(Project.class);
        Join<Project, Task> taskJoin = projectRoot.join(Project_.tasks);

        query.where(cb.equal(taskJoin.get(Task_.id), taskId));
        return getEntityManager().createQuery(query).getSingleResult(); // expecting exception if no result
    }

    public List<Long> listProjectIdsByNotFiredMemberUserId(Long userId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> idsQuery = cb.createQuery(Long.class);

        Root<Project> projectRoot = idsQuery.from(Project.class);
        Join<Project, ProjectMember> memberJoin = projectRoot.join(Project_.members);
        Join<ProjectMember, User> userJoin = memberJoin.join(ProjectMember_.user);

        idsQuery.select(projectRoot.get(Project_.ID));
        idsQuery.where(cb.equal(userJoin.get(User_.id), userId),
                cb.equal(memberJoin.get(ProjectMember_.fired), false));
        return getResultList(idsQuery);
    }

    public List<Project> listAndFetchTags() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> root = query.from(Project.class);
        root.fetch(Project_.tags, JoinType.LEFT);

        query = query.select(root);
        return getResultList(query);
    }

    public List<Project> listAndOrderAscByName() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> root = query.from(Project.class);

        query.orderBy(cb.asc(root.get(Project_.name)));
        return getResultList(query);
    }

    public List<Project> listPublicAndOrderAscByName() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> root = query.from(Project.class);

        query.where(cb.equal(root.get(Project_.isPrivate), false));
        query.orderBy(cb.asc(root.get(Project_.name)));
        return getResultList(query);
    }

    public List<Project> listAvailableToUserAndOrderAscByName(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> projectRoot = query.from(Project.class);
        Join<Project, ProjectMember> projectMemberJoin = projectRoot.join(Project_.members, JoinType.LEFT);

        List<Predicate> availabilityPredicates = new ArrayList<Predicate>(Arrays.asList(
                cb.and(
                        cb.equal(projectMemberJoin.get(ProjectMember_.fired), false),
                        cb.equal(projectMemberJoin.get(ProjectMember_.user), user))));
        availabilityPredicates.add(cb.equal(projectRoot.get(Project_.isPrivate), false));

        query.where(cb.or(
                cb.and(
                        cb.equal(projectMemberJoin.get(ProjectMember_.fired), false),
                        cb.equal(projectMemberJoin.get(ProjectMember_.user), user)),
                cb.equal(projectRoot.get(Project_.isPrivate), false)));
        query.distinct(true);
        query.orderBy(cb.asc(projectRoot.get(Project_.name)));
        return getResultList(query);
    }

    public List<Project> listByNotFiredProjectMemberUserAndOrderAscByName(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> projectRoot = query.from(Project.class);
        Join<Project, ProjectMember> memberJoin = projectRoot.join(Project_.members);

        query.where(cb.equal(memberJoin.get(ProjectMember_.user), user),
                cb.equal(memberJoin.get(ProjectMember_.fired), false));
        query.orderBy(cb.desc(memberJoin.get(ProjectMember_.manager)), cb.asc(projectRoot.get(Project_.name)));
        return getResultList(query);
    }

    public List<Project> listByNotFiredManagerProjectMemberUser(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> projectRoot = query.from(Project.class);
        Join<Project, ProjectMember> projectMemberJoin = projectRoot.join(Project_.members);

        query.where(cb.equal(projectMemberJoin.get(ProjectMember_.user), user),
                cb.equal(projectMemberJoin.get(ProjectMember_.fired), false),
                cb.equal(projectMemberJoin.get(ProjectMember_.manager), true));
        query.orderBy(cb.asc(projectRoot.get(Project_.name)));
        query.distinct(true);
        return getResultList(query);
    }

    public List<Project> searchPublicByNameAndFetchTagsAndOrderAscByName(String name) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> projectRoot = query.from(Project.class);
        projectRoot.fetch(Project_.tags, JoinType.LEFT);

        query.where(cb.like(cb.lower(projectRoot.get(Project_.name)), "%" + name.toLowerCase() + "%"),
                cb.equal(projectRoot.get(Project_.isPrivate), false));
        query.distinct(true);
        query.orderBy(cb.asc(projectRoot.get(Project_.name)));
        return getResultList(query);
    }
}
