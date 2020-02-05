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
import org.wrkr.clb.common.crypto.HashEncoder;
import org.wrkr.clb.common.mail.UserMailService;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.InviteStatus;
import org.wrkr.clb.model.InviteStatus.Status;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectInviteToken;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.InviteStatusRepo;
import org.wrkr.clb.repo.project.GrantedPermissionsProjectInviteRepo;
import org.wrkr.clb.repo.project.ProjectInviteTokenRepo;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.TokenRejectDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberDTO;
import org.wrkr.clb.services.dto.user.TokenRegisterDTO;
import org.wrkr.clb.services.project.GrantedPermissionsProjectInviteService;
import org.wrkr.clb.services.project.ProjectInviteTokenService;
import org.wrkr.clb.services.project.ProjectMemberService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.user.RegistrationService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.NotFoundException;

@Validated
@Service
public class DefaultGrantedPermissionsProjectInviteService implements GrantedPermissionsProjectInviteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultGrantedPermissionsProjectInviteService.class);

    private static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 400
        public static final String ALREADY_REGISTERED = "ALREADY_REGISTERED";
        public static final String STATUS_TRANSITION_NOT_ALLOWED = "STATUS_TRANSITION_NOT_ALLOWED";
    }

    @Autowired
    private GrantedPermissionsProjectInviteRepo projectInviteRepo;

    @Autowired
    private InviteStatusRepo inviteStatusRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private ProjectInviteTokenRepo inviteTokenRepo;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private ProjectInviteTokenService inviteTokenService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserMailService mailService;

    @Autowired
    private ElasticsearchUserService elasticsearchService;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private SecurityService authnSecurityService;

    private GrantedPermissionsProjectInvite create(User user, Project project, User currentUser,
            GrantedPermissionsProjectInviteDTO projectInviteDTO) {
        GrantedPermissionsProjectInvite invite = new GrantedPermissionsProjectInvite();
        invite.setUser(user);
        invite.setProject(project);
        invite.setSender(currentUser);

        InviteStatus status = inviteStatusRepo.getByNameCode(Status.PENDING);
        invite.setStatus(status);

        invite.setText(projectInviteDTO.text == null ? "" : projectInviteDTO.text);
        invite.setWriteAllowed(projectInviteDTO.writeAllowed || projectInviteDTO.manager);
        invite.setManager(projectInviteDTO.manager);

        OffsetDateTime now = DateTimeUtils.now();
        invite.setCreatedAt(now);
        invite.setUpdatedAt(now);

        projectInviteRepo.persist(invite);

        if (user != null) {
            try {
                elasticsearchService.addInvitedProject(user.getId(), invite.getProject().getId());
            } catch (Exception e) {
                LOG.error("Could not add invited project for user " + user.getId() + " in elasticsearch", e);
            }
        }

        return invite;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public GrantedPermissionsProjectInviteReadDTO create(User currentUser,
            GrantedPermissionsProjectInviteDTO projectInviteDTO)
            throws ApplicationException {
        // security start
        securityService.authzCanModifyProjectInvites(currentUser, projectInviteDTO.projectId);
        // security finish

        User user = userRepo.get(projectInviteDTO.userId);
        if (user == null) {
            throw new NotFoundException("User");
        }

        Project project = projectRepo.get(projectInviteDTO.projectId);
        if (project == null) {
            throw new NotFoundException("Project");
        }

        ProjectMember member = projectMemberRepo.getNotFiredByUserAndProject(user, project);
        if (member != null) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS,
                    "This user is already a member of this project.");
        }

        GrantedPermissionsProjectInvite invite = projectInviteRepo.getPendingByUserAndProject(user, project);
        if (invite != null) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS,
                    "This user is already invited to this project.");
        }

        invite = create(user, project, currentUser, projectInviteDTO);
        return GrantedPermissionsProjectInviteReadDTO.fromGrantedPermissionsInviteForProject(invite);
    }

    private void delete(GrantedPermissionsProjectInvite invite, User user) {
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
    public GrantedPermissionsProjectInviteReadDTO createByEmail(User currentUser,
            GrantedPermissionsProjectInviteDTO projectInviteDTO)
            throws ApplicationException {
        // security start
        securityService.authzCanModifyProjectInvites(currentUser, projectInviteDTO.projectId);
        // security finish

        if (userRepo.existsByEmail(projectInviteDTO.email)) {
            throw new BadRequestException(JsonStatusCode.ALREADY_REGISTERED, "User already registered.");
        }

        Project project = projectRepo.get(projectInviteDTO.projectId);
        if (project == null) {
            throw new NotFoundException("Project");
        }

        ProjectInviteToken inviteToken = inviteTokenRepo.getPendingByEmailAndProject(
                projectInviteDTO.email.toLowerCase(), project);
        if (inviteToken != null) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS,
                    "This email is already invited to this project.");
        }

        GrantedPermissionsProjectInvite invite = create(null, project, currentUser, projectInviteDTO);
        inviteToken = inviteTokenService.create(invite, projectInviteDTO.email.toLowerCase());

        boolean emailSent = mailService.sendProjectInviteEmail(inviteToken.getEmailAddress(),
                invite.getSender().getFullName(),
                invite.getProject().getName(), inviteToken.getPassword(), inviteToken.getToken()).emailSent;
        if (!emailSent) {
            try {
                inviteTokenRepo.delete(inviteToken);
                delete(invite, null);
            } catch (Exception e) {
                LOG.error("A server error occurred during deleting granted permissions project invite with token", e);
            }
        }
        return GrantedPermissionsProjectInviteReadDTO.fromGrantedPermissionsInviteForProject(invite, inviteToken,
                emailSent);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public GrantedPermissionsProjectInviteReadDTO update(User currentUser,
            GrantedPermissionsProjectInviteDTO inviteTokenDTO)
            throws ApplicationException {
        // security start
        securityService.authzCanModifyGrantedPermsInvite(currentUser, inviteTokenDTO.id);
        // security finish

        GrantedPermissionsProjectInvite invite = projectInviteRepo.getPending(inviteTokenDTO.id);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }

        invite.setWriteAllowed(inviteTokenDTO.writeAllowed || inviteTokenDTO.manager);
        invite.setManager(inviteTokenDTO.manager);

        invite = projectInviteRepo.merge(invite);
        return GrantedPermissionsProjectInviteReadDTO.fromGrantedPermissionsInvite(invite);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public GrantedPermissionsProjectInviteReadDTO get(User currentUser, Long id) throws Exception {
        // security start
        securityService.authzCanModifyGrantedPermsInvite(currentUser, id);
        // security finish

        GrantedPermissionsProjectInvite invite = projectInviteRepo
                .getPendingAndFetchUserAndProjectAndToken(id);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }

        return GrantedPermissionsProjectInviteReadDTO.fromGrantedPermissionsInviteWithUserAndProject(invite);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<GrantedPermissionsProjectInviteReadDTO> listByUser(User currentUser) {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        List<GrantedPermissionsProjectInvite> inviteList = projectInviteRepo
                .listPendingByUserAndFetchProjectAndSender(currentUser);
        return GrantedPermissionsProjectInviteReadDTO.fromGrantedPermissionsInvitesForUser(inviteList);
    }

    private void accept(User user, GrantedPermissionsProjectInvite invite) throws Exception {
        projectMemberService.create(user, invite.getProject(),
                new ProjectMemberDTO(invite.isWriteAllowed(), invite.isManager()));
        delete(invite, user);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void accept(User currentUser, Long id) throws Exception {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        GrantedPermissionsProjectInvite invite = projectInviteRepo
                .getByIdAndUserAndFetchProjectAndStatus(
                        id, currentUser);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }
        if (!InviteStatus.Status.PENDING.equals(invite.getStatus().getNameCode())) {
            throw new BadRequestException(JsonStatusCode.STATUS_TRANSITION_NOT_ALLOWED,
                    "Invite with this status cannot be accepted");
        }

        accept(currentUser, invite);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void acceptByToken(TokenRegisterDTO dto) throws Exception {
        ProjectInviteToken inviteToken = inviteTokenRepo
                .getByTokenAndFetchInviteAndUserAndProjectAndStatus(dto.token.strip());
        if (inviteToken == null) {
            throw new NotFoundException("Project invite token");
        }

        GrantedPermissionsProjectInvite invite = inviteToken.getInvite();
        if (!InviteStatus.Status.PENDING.equals(invite.getStatus().getNameCode())) {
            throw new BadRequestException(JsonStatusCode.STATUS_TRANSITION_NOT_ALLOWED,
                    "Invite with this status cannot be accepted");
        }

        String email = inviteToken.getEmailAddress();
        User user = userRepo.getByEmail(email);
        if (user == null) {
            String passwordHash = inviteToken.getPasswordHash();
            if (passwordHash != null) {
                if (dto.password != null) {
                    throw new BadRequestException(JsonStatusCode.DEPRECATED, "Invalid input.");
                }
            } else {
                if (dto.password.isBlank()) {
                    throw new BadRequestException(JsonStatusCode.CONSTRAINT_VIOLATION, "Invalid input.");
                }
                passwordHash = HashEncoder.encryptToHex(dto.password);
            }

            user = registrationService.createUserWithEmailAndPasswordHash(
                    email, passwordHash, dto.username, dto.fullName.strip(), false);

        }

        inviteTokenRepo.delete(inviteToken);
        accept(user, invite);
    }

    private GrantedPermissionsProjectInviteReadDTO reject(GrantedPermissionsProjectInvite invite, boolean reported)
            throws ApplicationException {
        if (!InviteStatus.Status.PENDING.equals(invite.getStatus().getNameCode())) {
            throw new BadRequestException(JsonStatusCode.STATUS_TRANSITION_NOT_ALLOWED,
                    "Invite with this status cannot be accepted");
        }

        invite.setStatus(inviteStatusRepo.getByNameCode(Status.REJECTED));
        invite.setReported(reported);
        invite.setUpdatedAt(DateTimeUtils.now());

        invite = projectInviteRepo.merge(invite);

        User user = invite.getUser();
        if (user != null) {
            try {
                elasticsearchService.removeInvitedProject(user.getId(), invite.getProject().getId());
            } catch (Exception e) {
                LOG.error("Could not remove invited project for user " + user.getId() + " in elasticsearch", e);
            }
        }

        return GrantedPermissionsProjectInviteReadDTO.fromGrantedPermissionsInvite(invite);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public GrantedPermissionsProjectInviteReadDTO reject(User currentUser, RejectDTO rejectDTO)
            throws ApplicationException {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        GrantedPermissionsProjectInvite invite = projectInviteRepo
                .getByIdAndUserAndFetchProjectAndStatus(
                        rejectDTO.id, currentUser);
        if (invite == null) {
            throw new NotFoundException("Project invite");
        }
        return reject(invite, rejectDTO.reported);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public GrantedPermissionsProjectInviteReadDTO rejectByToken(TokenRejectDTO rejectDTO) throws Exception {
        ProjectInviteToken inviteToken = inviteTokenRepo
                .getByTokenAndFetchInviteAndUserAndProjectAndStatus(rejectDTO.token.strip());
        if (inviteToken == null) {
            throw new NotFoundException("Project invite token");
        }

        GrantedPermissionsProjectInvite invite = inviteToken.getInvite();
        return reject(invite, rejectDTO.reported);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<GrantedPermissionsProjectInviteReadDTO> listByProjectId(User currentUser, Long projectId,
            boolean includeEmail) {
        // security start
        securityService.authzCanModifyProjectInvites(currentUser, projectId);
        // security finish

        List<GrantedPermissionsProjectInvite> inviteList = projectInviteRepo
                .listPendingByProjectIdAndFetchUserAndTokenAndSender(projectId, includeEmail);
        InviteStatus pendingStatus = inviteStatusRepo.getByNameCode(InviteStatus.Status.PENDING);
        return GrantedPermissionsProjectInviteReadDTO.fromGrantedPermissionsInvitesForProject(inviteList,
                pendingStatus);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<GrantedPermissionsProjectInviteReadDTO> listByProjectUiId(
            User currentUser, String projectUiId, boolean includeEmail) {
        // security start
        securityService.authzCanModifyProjectInvites(currentUser, projectUiId);
        // security finish

        List<GrantedPermissionsProjectInvite> inviteList = projectInviteRepo
                .listPendingByProjectUiIdAndFetchUserAndTokenAndSender(projectUiId, includeEmail);
        InviteStatus pendingStatus = inviteStatusRepo.getByNameCode(InviteStatus.Status.PENDING);
        return GrantedPermissionsProjectInviteReadDTO.fromGrantedPermissionsInvitesForProject(inviteList,
                pendingStatus);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void cancel(User currentUser, Long id) {
        // security start
        securityService.authzCanModifyGrantedPermsInvite(currentUser, id);
        // security finish

        GrantedPermissionsProjectInvite invite = projectInviteRepo.getAndFetchUserAndProjectAndToken(id);
        ProjectInviteToken inviteToken = invite.getToken();
        if (inviteToken != null) {
            inviteTokenRepo.delete(inviteToken);
        }
        delete(invite, invite.getUser());
    }
}
