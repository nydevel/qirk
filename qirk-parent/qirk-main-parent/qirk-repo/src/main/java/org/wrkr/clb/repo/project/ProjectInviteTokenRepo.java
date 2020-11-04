package org.wrkr.clb.repo.project;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite_;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.InviteStatus_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectInviteToken;
import org.wrkr.clb.model.project.ProjectInviteToken_;
import org.wrkr.clb.repo.JPADeletingRepo;

@Repository
public class ProjectInviteTokenRepo extends JPADeletingRepo<ProjectInviteToken> {

    @Override
    public ProjectInviteToken get(Long id) {
        return get(ProjectInviteToken.class, id);
    }

    public String getTokenIfExists(String token) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);

        Root<ProjectInviteToken> inviteTokenRoot = query.from(ProjectInviteToken.class);

        query.select(inviteTokenRoot.get(ProjectInviteToken_.TOKEN));
        query.where(cb.equal(inviteTokenRoot.get(ProjectInviteToken_.token), token));
        return getSingleResultOrNull(query);
    }

    public String getEmailByToken(String token) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);

        Root<ProjectInviteToken> inviteTokenRoot = query.from(ProjectInviteToken.class);

        query.select(inviteTokenRoot.get(ProjectInviteToken_.EMAIL_ADDRESS));
        query.where(cb.equal(inviteTokenRoot.get(ProjectInviteToken_.token), token));
        return getSingleResultOrNull(query);
    }

    public ProjectInviteToken getByTokenAndFetchInviteAndUserAndProjectAndStatus(String token) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInviteToken> query = cb.createQuery(ProjectInviteToken.class);

        Root<ProjectInviteToken> inviteTokenRoot = query.from(ProjectInviteToken.class);
        Fetch<ProjectInviteToken, GrantedPermissionsProjectInvite> inviteFetch = inviteTokenRoot
                .fetch(ProjectInviteToken_.invite);
        inviteFetch.fetch(GrantedPermissionsProjectInvite_.user, JoinType.LEFT);
        inviteFetch.fetch(GrantedPermissionsProjectInvite_.status);

        query.where(cb.equal(inviteTokenRoot.get(ProjectInviteToken_.token), token));
        return getSingleResultOrNull(query);
    }

    public ProjectInviteToken getPendingByEmailAndProject(String email, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectInviteToken> query = cb.createQuery(ProjectInviteToken.class);

        Root<ProjectInviteToken> inviteTokenRoot = query.from(ProjectInviteToken.class);
        Join<ProjectInviteToken, GrantedPermissionsProjectInvite> inviteJoin = inviteTokenRoot.join(ProjectInviteToken_.invite);
        Join<GrantedPermissionsProjectInvite, InviteStatus> statusJoin = inviteJoin.join(GrantedPermissionsProjectInvite_.status);

        query.where(cb.equal(inviteJoin.get(GrantedPermissionsProjectInvite_.project), project),
                cb.equal(inviteTokenRoot.get(ProjectInviteToken_.emailAddress), email),
                cb.equal(statusJoin.get(InviteStatus_.nameCode), InviteStatus.Status.PENDING));
        return getSingleResultOrNull(query);
    }
}
