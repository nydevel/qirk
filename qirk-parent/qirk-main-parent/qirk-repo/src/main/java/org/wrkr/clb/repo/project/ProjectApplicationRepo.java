package org.wrkr.clb.repo.project;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ApplicationStatus;
import org.wrkr.clb.model.project.ApplicationStatus.Status;
import org.wrkr.clb.model.project.ApplicationStatus_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.model.project.ProjectApplication_;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.JPADeletingRepo;

/**
 * @author Evgeny Poreykin
 *
 */
@Repository
public class ProjectApplicationRepo extends JPADeletingRepo<ProjectApplication> {

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
            applicationRoot.fetch(ProjectApplication_.project);
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

    public List<ProjectApplication> listByUserAndFetchProjectAndStatus(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectApplication> query = cb.createQuery(ProjectApplication.class);

        Root<ProjectApplication> applicationRoot = query.from(ProjectApplication.class);
        applicationRoot.fetch(ProjectApplication_.project);
        applicationRoot.fetch(ProjectApplication_.status);

        query.where(cb.equal(applicationRoot.get(ProjectApplication_.user), user));
        query.orderBy(cb.desc(applicationRoot.get(ProjectApplication_.updatedAt)));
        return getResultList(query);
    }
}
