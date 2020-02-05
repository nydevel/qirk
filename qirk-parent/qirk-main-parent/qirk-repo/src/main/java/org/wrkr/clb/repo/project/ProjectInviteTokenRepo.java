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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.InviteStatus;
import org.wrkr.clb.model.InviteStatus_;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectInviteToken;
import org.wrkr.clb.model.project.ProjectInviteToken_;
import org.wrkr.clb.repo.JPABaseDeletingRepo;

@Repository
public class ProjectInviteTokenRepo extends JPABaseDeletingRepo<ProjectInviteToken> {

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
