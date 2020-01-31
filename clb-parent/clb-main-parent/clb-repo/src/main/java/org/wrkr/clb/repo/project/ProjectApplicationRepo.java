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
package org.wrkr.clb.repo.project;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.ApplicationStatus;
import org.wrkr.clb.model.ApplicationStatus.Status;
import org.wrkr.clb.model.ApplicationStatus_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.model.project.ProjectApplication_;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.JPABaseDeletingRepo;


@Repository
public class ProjectApplicationRepo extends JPABaseDeletingRepo<ProjectApplication> {

    @Override
    public ProjectApplication get(Long id) {
        return get(ProjectApplication.class, id);
    }

    public ProjectApplication getPendingById(Long id, boolean fetchUserAndOrganization) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectApplication> query = cb.createQuery(ProjectApplication.class);

        Root<ProjectApplication> applicationRoot = query.from(ProjectApplication.class);
        if (fetchUserAndOrganization) {
            applicationRoot.fetch(ProjectApplication_.user);
            applicationRoot.fetch(ProjectApplication_.project).fetch(Project_.organization);
        }
        Join<ProjectApplication, ApplicationStatus> statusJoin = applicationRoot.join(ProjectApplication_.status);

        query.where(cb.equal(applicationRoot.get(ProjectApplication_.id), id),
                cb.equal(statusJoin.get(ApplicationStatus_.nameCode), Status.PENDING));
        return getSingleResultOrNull(query);
    }

    public ProjectApplication getByIdAndUser(Long id, User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectApplication> query = cb.createQuery(ProjectApplication.class);

        Root<ProjectApplication> applicationRoot = query.from(ProjectApplication.class);

        query.where(cb.equal(applicationRoot.get(ProjectApplication_.id), id),
                cb.equal(applicationRoot.get(ProjectApplication_.user), user));
        return getSingleResultOrNull(query);
    }

    public ProjectApplication getPendingByUserAndProject(User user, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectApplication> query = cb.createQuery(ProjectApplication.class);

        Root<ProjectApplication> applicationRoot = query.from(ProjectApplication.class);
        Join<ProjectApplication, ApplicationStatus> statusJoin = applicationRoot.join(ProjectApplication_.status);

        query.where(cb.equal(applicationRoot.get(ProjectApplication_.user), user),
                cb.equal(applicationRoot.get(ProjectApplication_.project), project),
                cb.equal(statusJoin.get(ApplicationStatus_.nameCode), Status.PENDING));
        return getSingleResultOrNull(query);
    }

    public ProjectApplication getLastByUserAndProject(User user, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectApplication> query = cb.createQuery(ProjectApplication.class);

        Root<ProjectApplication> applicationRoot = query.from(ProjectApplication.class);
        applicationRoot.fetch(ProjectApplication_.status);

        query.where(cb.equal(applicationRoot.get(ProjectApplication_.user), user),
                cb.equal(applicationRoot.get(ProjectApplication_.project), project));
        query.orderBy(cb.desc(applicationRoot.get(ProjectApplication_.updatedAt)));
        return getFirstResultOrNull(query);
    }

    public List<ProjectApplication> listPendingByProjectIdAndFetchUser(Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectApplication> query = cb.createQuery(ProjectApplication.class);

        Root<ProjectApplication> applicationRoot = query.from(ProjectApplication.class);
        applicationRoot.fetch(ProjectApplication_.user);
        Join<ProjectApplication, ApplicationStatus> statusJoin = applicationRoot.join(ProjectApplication_.status);
        Join<ProjectApplication, Project> projectJoin = applicationRoot.join(ProjectApplication_.project);

        query.where(cb.equal(projectJoin.get(Project_.id), projectId),
                cb.equal(statusJoin.get(ApplicationStatus_.nameCode), Status.PENDING));
        query.orderBy(cb.desc(applicationRoot.get(ProjectApplication_.updatedAt)));
        return getResultList(query);
    }

    public List<ProjectApplication> listPendingByProjectUiIdAndFetchUser(String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectApplication> query = cb.createQuery(ProjectApplication.class);

        Root<ProjectApplication> applicationRoot = query.from(ProjectApplication.class);
        applicationRoot.fetch(ProjectApplication_.user);
        Join<ProjectApplication, ApplicationStatus> statusJoin = applicationRoot.join(ProjectApplication_.status);
        Join<ProjectApplication, Project> projectJoin = applicationRoot.join(ProjectApplication_.project);

        query.where(cb.equal(projectJoin.get(Project_.uiId), projectUiId),
                cb.equal(statusJoin.get(ApplicationStatus_.nameCode), Status.PENDING));
        query.orderBy(cb.desc(applicationRoot.get(ProjectApplication_.updatedAt)));
        return getResultList(query);
    }

    public List<ProjectApplication> listByUserAndFetchProjectAndOrganizationAndStatus(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectApplication> query = cb.createQuery(ProjectApplication.class);

        Root<ProjectApplication> applicationRoot = query.from(ProjectApplication.class);
        applicationRoot.fetch(ProjectApplication_.project).fetch(Project_.organization);
        applicationRoot.fetch(ProjectApplication_.status);

        query.where(cb.equal(applicationRoot.get(ProjectApplication_.user), user));
        query.orderBy(cb.desc(applicationRoot.get(ProjectApplication_.updatedAt)));
        return getResultList(query);
    }
}
