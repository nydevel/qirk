package org.wrkr.clb.repo.project;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.InviteStatus_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectInvite;
import org.wrkr.clb.model.project.ProjectInvite_;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.User_;
import org.wrkr.clb.repo.JPADeletingRepo;

@Repository
public class ProjectInviteRepo extends JPADeletingRepo<ProjectInvite> {

    @Override
    public ProjectInvite get(Long id) {
        return get(ProjectInvite.class, id);
    }

    public ProjectInvite getAndFetchProject(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        inviteRoot.fetch(ProjectInvite_.project);

        query.where(cb.equal(inviteRoot.get(ProjectInvite_.id), id));
        return getSingleResultOrNull(query);
    }

    public ProjectInvite getAndFetchUserAndProject(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        inviteRoot.fetch(ProjectInvite_.user);
        inviteRoot.fetch(ProjectInvite_.project);

        query.where(cb.equal(inviteRoot.get(ProjectInvite_.id), id));
        return getSingleResultOrNull(query);
    }

    public ProjectInvite getAndFetchUserAndStatus(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        inviteRoot.fetch(ProjectInvite_.user);
        inviteRoot.fetch(ProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(ProjectInvite_.id), id));
        return getSingleResultOrNull(query);
    }

    public ProjectInvite getByIdAndUserAndFetchProjectAndStatus(Long id, User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        inviteRoot.fetch(ProjectInvite_.project);
        inviteRoot.fetch(ProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(ProjectInvite_.id), id),
                cb.equal(inviteRoot.get(ProjectInvite_.user), user));
        return getSingleResultOrNull(query);
    }

    public ProjectInvite getByUserAndProject(User user, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);

        query.where(cb.equal(inviteRoot.get(ProjectInvite_.user), user),
                cb.equal(inviteRoot.get(ProjectInvite_.project), project));
        return getSingleResultOrNull(query);
    }

    public ProjectInvite getPendingOrAcceptedByUserAndProject(User user, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        Join<ProjectInvite, InviteStatus> statusJoin = inviteRoot.join(ProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(ProjectInvite_.user), user),
                cb.equal(inviteRoot.get(ProjectInvite_.project), project),
                statusJoin.get(InviteStatus_.nameCode)
                        .in(InviteStatus.Status.PENDING, InviteStatus.Status.ACCEPTED));
        return getSingleResultOrNull(query);
    }

    public List<ProjectInvite> listNotReportedByUserAndFetchProjectAndStatus(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        inviteRoot.fetch(ProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(ProjectInvite_.user), user),
                cb.equal(inviteRoot.get(ProjectInvite_.reported), false));
        query.orderBy(cb.desc(inviteRoot.get(ProjectInvite_.updatedAt)));
        return getResultList(query);
    }

    public List<ProjectInvite> listByProjectIdAndFetchUserAndStatus(Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        inviteRoot.fetch(ProjectInvite_.user);
        inviteRoot.fetch(ProjectInvite_.status);
        Join<ProjectInvite, Project> projectJoin = inviteRoot.join(ProjectInvite_.project);

        query.where(cb.equal(projectJoin.get(Project_.id), projectId));
        query.orderBy(cb.desc(inviteRoot.get(ProjectInvite_.updatedAt)));
        return getResultList(query);
    }

    public List<ProjectInvite> listByProjectUiIdAndFetchUserAndStatus(String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> query = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        inviteRoot.fetch(ProjectInvite_.user);
        inviteRoot.fetch(ProjectInvite_.status);
        Join<ProjectInvite, Project> projectJoin = inviteRoot.join(ProjectInvite_.project);

        query.where(cb.equal(projectJoin.get(Project_.uiId), projectUiId));
        query.orderBy(cb.desc(inviteRoot.get(ProjectInvite_.updatedAt)));
        return getResultList(query);
    }

    @Deprecated
    public List<Tuple> listProjectIdAndInviteStatusByProjectIdsAndUserId(List<Long> projectIds, long userId) {
        if (projectIds.size() == 0) {
            return new ArrayList<Tuple>();
        }

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ProjectInvite> inviteRoot = query.from(ProjectInvite.class);
        Join<ProjectInvite, InviteStatus> statusJoin = inviteRoot.join(ProjectInvite_.status);
        Join<ProjectInvite, User> userJoin = inviteRoot.join(ProjectInvite_.user);
        Join<ProjectInvite, Project> projectJoin = inviteRoot.join(ProjectInvite_.project);

        query.multiselect(projectJoin.get(Project_.ID), inviteRoot, statusJoin);
        query.where(cb.equal(userJoin.get(User_.id), userId), projectJoin.get(Project_.id).in(projectIds));
        query.orderBy(cb.asc(inviteRoot.get(ProjectInvite_.updatedAt)));
        return getResultList(query);
    }
}
