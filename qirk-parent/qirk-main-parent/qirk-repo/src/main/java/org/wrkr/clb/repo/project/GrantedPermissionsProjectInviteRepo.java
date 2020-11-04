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
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite_;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.InviteStatus_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.JPADeletingRepo;

@Repository
public class GrantedPermissionsProjectInviteRepo extends JPADeletingRepo<GrantedPermissionsProjectInvite> {

    @Override
    public GrantedPermissionsProjectInvite get(Long id) {
        return get(GrantedPermissionsProjectInvite.class, id);
    }

    public GrantedPermissionsProjectInvite getAndFetchUserAndProjectAndToken(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<GrantedPermissionsProjectInvite> query = cb.createQuery(GrantedPermissionsProjectInvite.class);

        Root<GrantedPermissionsProjectInvite> inviteRoot = query.from(GrantedPermissionsProjectInvite.class);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.tokens, JoinType.LEFT);

        query.where(cb.equal(inviteRoot.get(GrantedPermissionsProjectInvite_.id), id));
        return getSingleResultOrNull(query);
    }

    public GrantedPermissionsProjectInvite getPending(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<GrantedPermissionsProjectInvite> query = cb.createQuery(GrantedPermissionsProjectInvite.class);

        Root<GrantedPermissionsProjectInvite> inviteRoot = query.from(GrantedPermissionsProjectInvite.class);
        Join<GrantedPermissionsProjectInvite, InviteStatus> statusJoin = inviteRoot.join(GrantedPermissionsProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(GrantedPermissionsProjectInvite_.id), id),
                cb.equal(statusJoin.get(InviteStatus_.nameCode), InviteStatus.Status.PENDING));
        return getSingleResultOrNull(query);
    }

    public GrantedPermissionsProjectInvite getPendingAndFetchUserAndProjectAndToken(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<GrantedPermissionsProjectInvite> query = cb.createQuery(GrantedPermissionsProjectInvite.class);

        Root<GrantedPermissionsProjectInvite> inviteRoot = query.from(GrantedPermissionsProjectInvite.class);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.user, JoinType.LEFT);
        Join<GrantedPermissionsProjectInvite, InviteStatus> statusJoin = inviteRoot.join(GrantedPermissionsProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(GrantedPermissionsProjectInvite_.id), id),
                cb.equal(statusJoin.get(InviteStatus_.nameCode), InviteStatus.Status.PENDING));
        return getSingleResultOrNull(query);
    }

    public GrantedPermissionsProjectInvite getByIdAndUserAndFetchProjectAndStatus(Long id, User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<GrantedPermissionsProjectInvite> query = cb.createQuery(GrantedPermissionsProjectInvite.class);

        Root<GrantedPermissionsProjectInvite> inviteRoot = query.from(GrantedPermissionsProjectInvite.class);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(GrantedPermissionsProjectInvite_.id), id),
                cb.equal(inviteRoot.get(GrantedPermissionsProjectInvite_.user), user));

        GrantedPermissionsProjectInvite result = getSingleResultOrNull(query);
        if (result != null) {
            result.setUser(user);
        }
        return result;
    }

    public GrantedPermissionsProjectInvite getPendingByUserAndProject(User user, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<GrantedPermissionsProjectInvite> query = cb.createQuery(GrantedPermissionsProjectInvite.class);

        Root<GrantedPermissionsProjectInvite> inviteRoot = query.from(GrantedPermissionsProjectInvite.class);
        Join<GrantedPermissionsProjectInvite, InviteStatus> statusJoin = inviteRoot.join(GrantedPermissionsProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(GrantedPermissionsProjectInvite_.user), user),
                cb.equal(inviteRoot.get(GrantedPermissionsProjectInvite_.project), project),
                cb.equal(statusJoin.get(InviteStatus_.nameCode), InviteStatus.Status.PENDING));
        return getSingleResultOrNull(query);
    }

    public List<GrantedPermissionsProjectInvite> listPendingByUserAndFetchProjectAndSender(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<GrantedPermissionsProjectInvite> query = cb.createQuery(GrantedPermissionsProjectInvite.class);

        Root<GrantedPermissionsProjectInvite> inviteRoot = query.from(GrantedPermissionsProjectInvite.class);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.sender);
        Join<GrantedPermissionsProjectInvite, InviteStatus> statusJoin = inviteRoot.join(GrantedPermissionsProjectInvite_.status);

        query.where(cb.equal(inviteRoot.get(GrantedPermissionsProjectInvite_.user), user),
                cb.equal(statusJoin.get(InviteStatus_.nameCode), InviteStatus.Status.PENDING));
        return getResultList(query);
    }

    public List<GrantedPermissionsProjectInvite> listPendingByProjectIdAndFetchUserAndTokenAndSender(
            Long projectId, boolean includeEmail) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<GrantedPermissionsProjectInvite> query = cb.createQuery(GrantedPermissionsProjectInvite.class);

        Root<GrantedPermissionsProjectInvite> inviteRoot = query.from(GrantedPermissionsProjectInvite.class);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.user, JoinType.LEFT);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.tokens, JoinType.LEFT);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.sender, JoinType.LEFT);
        Join<GrantedPermissionsProjectInvite, InviteStatus> statusJoin = inviteRoot.join(GrantedPermissionsProjectInvite_.status);
        Join<GrantedPermissionsProjectInvite, Project> projectJoin = inviteRoot.join(GrantedPermissionsProjectInvite_.project);

        List<Predicate> predicates = new ArrayList<Predicate>(Arrays.asList(
                cb.equal(projectJoin.get(Project_.id), projectId),
                cb.equal(statusJoin.get(InviteStatus_.nameCode), InviteStatus.Status.PENDING)));
        if (!includeEmail) {
            predicates.add(cb.isNotNull(inviteRoot.get(GrantedPermissionsProjectInvite_.user)));
        }
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(inviteRoot.get(GrantedPermissionsProjectInvite_.updatedAt)));
        return getResultList(query);
    }

    public List<GrantedPermissionsProjectInvite> listPendingByProjectUiIdAndFetchUserAndTokenAndSender(
            String projectUiId, boolean includeEmail) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<GrantedPermissionsProjectInvite> query = cb.createQuery(GrantedPermissionsProjectInvite.class);

        Root<GrantedPermissionsProjectInvite> inviteRoot = query.from(GrantedPermissionsProjectInvite.class);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.user, JoinType.LEFT);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.tokens, JoinType.LEFT);
        inviteRoot.fetch(GrantedPermissionsProjectInvite_.sender, JoinType.LEFT);
        Join<GrantedPermissionsProjectInvite, InviteStatus> statusJoin = inviteRoot.join(GrantedPermissionsProjectInvite_.status);
        Join<GrantedPermissionsProjectInvite, Project> projectJoin = inviteRoot.join(GrantedPermissionsProjectInvite_.project);

        List<Predicate> predicates = new ArrayList<Predicate>(Arrays.asList(
                cb.equal(projectJoin.get(Project_.uiId), projectUiId),
                cb.equal(statusJoin.get(InviteStatus_.nameCode), InviteStatus.Status.PENDING)));
        if (!includeEmail) {
            predicates.add(cb.isNotNull(inviteRoot.get(GrantedPermissionsProjectInvite_.user)));
        }
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(inviteRoot.get(GrantedPermissionsProjectInvite_.updatedAt)));
        return getResultList(query);
    }
}
