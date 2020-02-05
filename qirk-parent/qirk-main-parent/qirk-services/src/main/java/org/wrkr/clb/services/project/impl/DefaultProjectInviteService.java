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
package org.wrkr.clb.services.project.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.InviteStatus;
import org.wrkr.clb.model.InviteStatus.Status;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectInvite;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.InviteStatusRepo;
import org.wrkr.clb.repo.project.ProjectInviteRepo;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberPermissionsDTO;
import org.wrkr.clb.services.organization.OrganizationMemberService;
import org.wrkr.clb.services.project.ProjectInviteService;
import org.wrkr.clb.services.project.ProjectMemberService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.NotFoundException;


@Validated
@Service
public class DefaultProjectInviteService implements ProjectInviteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultProjectInviteService.class);

    private static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 400
        public static final String STATUS_TRANSITION_NOT_ALLOWED = "STATUS_TRANSITION_NOT_ALLOWED";
    }

    @Autowired
    private ProjectInviteRepo projectInviteRepo;

    @Autowired
    private InviteStatusRepo inviteStatusRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private OrganizationMemberService organizationMemberService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private ElasticsearchUserService elasticsearchService;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private SecurityService authnSecurityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public ProjectInviteReadDTO create(User currentUser, ProjectInviteDTO projectInviteDTO)
            throws ApplicationException {
        // security start
        securityService.authzCanModifyProjectInvites(currentUser, projectInviteDTO.projectId);
        // security finish

        User user = userRepo.get(projectInviteDTO.userId);
        if (user == null) {
            throw new NotFoundException("User");
        }

        Project project = projectRepo.getAndFetchOrganization(projectInviteDTO.projectId);
        if (project == null) {
            throw new NotFoundException("Project");
        }

        ProjectMember member = projectMemberRepo.getNotFiredByUserAndProject(user, project);
        if (member != null) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS,
                    "This user is already a member of this project.");
        }

        ProjectInvite invite = projectInviteRepo.getPendingOrAcceptedByUserAndProject(user, project);
        if (invite != null) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS,
                    "This user is already invited to this project.");
        }

        invite = new ProjectInvite();
        invite.setUser(user);
        invite.setProject(project);
        invite.setSender(currentUser);

        InviteStatus status = inviteStatusRepo.getByNameCode(Status.PENDING);
        invite.setStatus(status);

        invite.setText(projectInviteDTO.text);

        OffsetDateTime now = DateTimeUtils.now();
        invite.setCreatedAt(now);
        invite.setUpdatedAt(now);

        projectInviteRepo.persist(invite);

        try {
            elasticsearchService.addInvitedProject(user.getId(), invite.getProject().getId());
        } catch (Exception e) {
            LOG.error("Could not add invited project for user " + user.getId() + " in elasticsearch", e);
        }

        return ProjectInviteReadDTO.fromInvite(invite, true, true, true);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ProjectInviteReadDTO get(User currentUser, Long id) throws ApplicationException {
        // security start
        securityService.authzCanModifyProjectInvite(currentUser, id);
        // security finish

        ProjectInvite invite = projectInviteRepo.getAndFetchUserAndStatus(id);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }
        return ProjectInviteReadDTO.fromInvite(invite, true, true, true);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<ProjectInviteReadDTO> listByUser(User currentUser) {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        List<ProjectInvite> inviteList = projectInviteRepo
                .listNotReportedByUserAndFetchProjectAndStatus(currentUser);
        return ProjectInviteReadDTO.fromInvitesForUser(inviteList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public ProjectInviteReadDTO accept(User currentUser, Long id) throws ApplicationException {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        ProjectInvite invite = projectInviteRepo.getByIdAndUserAndFetchProjectAndStatus(id, currentUser);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }
        if (!InviteStatus.Status.PENDING.equals(invite.getStatus().getNameCode())
                && !InviteStatus.Status.REJECTED.equals(invite.getStatus().getNameCode())) {
            throw new BadRequestException(JsonStatusCode.STATUS_TRANSITION_NOT_ALLOWED,
                    "Invite with this status cannot be accepted");
        }

        invite.setStatus(inviteStatusRepo.getByNameCode(Status.ACCEPTED));
        invite.setUpdatedAt(DateTimeUtils.now());

        invite = projectInviteRepo.merge(invite);
        return ProjectInviteReadDTO.frominvite(invite);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public ProjectInviteReadDTO reject(User currentUser, RejectDTO rejectDTO) throws ApplicationException {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        ProjectInvite invite = projectInviteRepo.getByIdAndUserAndFetchProjectAndStatus(rejectDTO.id, currentUser);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }
        if (!InviteStatus.Status.PENDING.equals(invite.getStatus().getNameCode())) {
            throw new BadRequestException(JsonStatusCode.STATUS_TRANSITION_NOT_ALLOWED,
                    "Invite with this status cannot be rejected");
        }

        invite.setStatus(inviteStatusRepo.getByNameCode(Status.REJECTED));
        invite.setReported(rejectDTO.reported);
        invite.setUpdatedAt(DateTimeUtils.now());

        invite = projectInviteRepo.merge(invite);

        if (currentUser != null) {
            try {
                elasticsearchService.removeInvitedProject(currentUser.getId(), invite.getProject().getId());
            } catch (Exception e) {
                LOG.error("Could not remove invited project for user " + currentUser.getId() + " in elasticsearch", e);
            }
        }

        return ProjectInviteReadDTO.frominvite(invite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectInviteReadDTO> listByProjectId(User currentUser, Long projectId) {
        // security start
        securityService.authzCanModifyProjectInvites(currentUser, projectId);
        // security finish

        List<ProjectInvite> inviteList = projectInviteRepo.listByProjectIdAndFetchUserAndStatus(projectId);
        return ProjectInviteReadDTO.fromInvitesForProject(inviteList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectInviteReadDTO> listByProjectUiId(User currentUser, String projectUiId) {
        // security start
        securityService.authzCanModifyProjectInvites(currentUser, projectUiId);
        // security finish

        List<ProjectInvite> inviteList = projectInviteRepo.listByProjectUiIdAndFetchUserAndStatus(projectUiId);
        return ProjectInviteReadDTO.fromInvitesForProject(inviteList);
    }

    private void delete(ProjectInvite invite) {
        User user = invite.getUser();
        projectInviteRepo.delete(invite);

        if (user != null) {
            try {
                elasticsearchService.removeInvitedProject(user.getId(), invite.getProject().getId());
            } catch (Exception e) {
                LOG.error("Could not remove invited project for user " + user.getId() + " in elasticsearch", e);
            }
        }
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void cancel(User currentUser, Long id) throws ApplicationException {
        // security start
        securityService.authzCanModifyProjectInvite(currentUser, id);
        // security finish

        ProjectInvite invite = projectInviteRepo.get(id);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }

        delete(invite);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void execute(User currentUser, ProjectMemberPermissionsDTO inviteDTO) throws Exception {
        // security start
        securityService.authzCanModifyProjectInvite(currentUser, inviteDTO.id);
        // security finish

        ProjectInvite invite = projectInviteRepo.getAndFetchUserAndStatus(inviteDTO.id);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }
        if (!InviteStatus.Status.ACCEPTED.equals(invite.getStatus().getNameCode())) {
            throw new BadRequestException(JsonStatusCode.STATUS_TRANSITION_NOT_ALLOWED,
                    "Invite with this status cannot be executed");
        }

        OrganizationMember organizationMember = organizationMemberService.create(
                invite.getUser(), invite.getProject().getOrganization(), new OrganizationMemberDTO(), true);
        projectMemberService.create(invite.getProject(), organizationMember,
                new ProjectMemberDTO(inviteDTO.writeAllowed, inviteDTO.manager));

        delete(invite);
    }
}
