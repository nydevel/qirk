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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.TokenGenerator;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;
import org.wrkr.clb.common.jms.message.statistics.ProjectDocUpdateMessage;
import org.wrkr.clb.common.jms.services.StatisticsSender;
import org.wrkr.clb.common.util.chat.ChatType;
import org.wrkr.clb.common.util.strings.MarkdownUtils;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.model.project.ProjectInvite;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.JDBCProjectRepo;
import org.wrkr.clb.repo.project.ProjectApplicationRepo;
import org.wrkr.clb.repo.project.ProjectInviteRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.project.task.ProjectTaskNumberSequenceRepo;
import org.wrkr.clb.repo.project.task.TaskSubscriberRepo;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.project.ProjectApplicationStatusDTO;
import org.wrkr.clb.services.dto.project.ProjectDTO;
import org.wrkr.clb.services.dto.project.ProjectDocDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteOptionDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteStatusDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberDTO;
import org.wrkr.clb.services.dto.project.ProjectNameAndUiIdDTO;
import org.wrkr.clb.services.dto.project.ProjectReadDTO;
import org.wrkr.clb.services.impl.VersionedEntityService;
import org.wrkr.clb.services.project.ProjectMemberService;
import org.wrkr.clb.services.project.ProjectService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.user.UserFavoriteService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultProjectService extends VersionedEntityService implements ProjectService {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DefaultProjectService.class);

    // chat token config values
    private Integer chatTokenNotBeforeToleranceSeconds;
    private Integer chatTokenLifetimeSeconds;

    public Integer getChatTokenNotBeforeToleranceSeconds() {
        return chatTokenNotBeforeToleranceSeconds;
    }

    public void setChatTokenNotBeforeToleranceSeconds(Integer chatTokenNotBeforeToleranceSeconds) {
        this.chatTokenNotBeforeToleranceSeconds = chatTokenNotBeforeToleranceSeconds;
    }

    public Integer getChatTokenLifetimeSeconds() {
        return chatTokenLifetimeSeconds;
    }

    public void setChatTokenLifetimeSeconds(Integer chatTokenLifetimeSeconds) {
        this.chatTokenLifetimeSeconds = chatTokenLifetimeSeconds;
    }

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private JDBCProjectRepo jdbcProjectRepo;

    @Autowired
    private ProjectTaskNumberSequenceRepo taskNumberSequenceRepo;

    @Autowired
    private ProjectInviteRepo projectInviteRepo;

    @Autowired
    private ProjectApplicationRepo projectApplicationRepo;

    @Autowired
    private TaskSubscriberRepo taskSubscriberRepo;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private UserFavoriteService userFavoriteService;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private SecurityService authnSecurityService;

    @Autowired
    private StatisticsSender statisticsSender;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ExistsDTO checkUiId(User currentUser, String uiId) {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        return new ExistsDTO(projectRepo.existsByUiId(uiId.toLowerCase()));
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public Project create(User creatorUser, ProjectDTO projectDTO, List<User> membersToCreate)
            throws Exception {
        Project project = new Project();
        project.setOwner(creatorUser);
        project.setPrivate(projectDTO.isPrivate);

        ProjectTaskNumberSequence taskNumberSequence = new ProjectTaskNumberSequence();
        taskNumberSequence = taskNumberSequenceRepo.save(taskNumberSequence);
        project.setTaskNumberSequence(taskNumberSequence);

        String descriptionMd = projectDTO.description.strip();
        String descriptionHtml = MarkdownUtils.markdownToHtml(descriptionMd);
        project.setDescriptionMd(descriptionMd);
        project.setDescriptionHtml(descriptionHtml);

        project.setName(projectDTO.name);
        project.setKey(projectDTO.key);
        project.setDocumentationMd("");
        project.setDocumentationHtml("");

        String uiId = projectDTO.uiId.strip().toLowerCase();
        if (uiId.isEmpty()) {
            do {
                uiId = RandomStringUtils.randomAlphanumeric(Project.UI_ID_LENGTH).toLowerCase();
            } while (projectRepo.existsByUiId(uiId));
        }
        project.setUiId(uiId);

        projectRepo.persist(project);

        for (User user : membersToCreate) {
            projectMemberService.create(user, project, new ProjectMemberDTO(true, true));
        }

        return project;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public ProjectReadDTO create(User currentUser, ProjectDTO projectDTO) throws Exception {
        // security start
        securityService.authzCanCreateProject(currentUser);
        // security finish

        List<User> membersToCreate = Arrays.asList(currentUser);
        Project project = create(currentUser, projectDTO, membersToCreate);

        return ProjectReadDTO.fromEntityWithDescription(project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public ProjectReadDTO update(User currentUser, ProjectDTO projectDTO) throws ApplicationException {
        // security start
        securityService.authzCanUpdateProject(currentUser, projectDTO.id);
        // security finish

        Project project = projectRepo.get(projectDTO.id);
        if (project == null) {
            throw new NotFoundException("Project");
        }
        project = checkRecordVersion(project, projectDTO.recordVersion);

        String descriptionMd = projectDTO.description.strip();
        String descriptionHtml = MarkdownUtils.markdownToHtml(descriptionMd);
        project.setDescriptionMd(descriptionMd);
        project.setDescriptionHtml(descriptionHtml);

        project.setName(projectDTO.name);
        if (!project.getKey().isEmpty()) {
            project.setKey(projectDTO.key);
        }
        boolean wasPrivate = project.isPrivate();
        project.setPrivate(projectDTO.isPrivate);

        if (!projectDTO.uiId.isBlank()) {
            project.setUiId(projectDTO.uiId.strip().toLowerCase());
        }

        project = projectRepo.merge(project);

        if (!wasPrivate && project.isPrivate()) {
            taskSubscriberRepo.deleteNonMembersByProjectId(project.getId());
        }

        return ProjectReadDTO.fromEntityWithDescription(project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ProjectDocDTO getDocumentation(User currentUser, Long id) throws ApplicationException {
        // security start
        securityService.authzCanReadProject(currentUser, id);
        // security finish

        Project project = jdbcProjectRepo.getByIdForDocumentation(id);
        if (project == null) {
            throw new NotFoundException("Project");
        }
        return ProjectDocDTO.fromEntity(project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ProjectDocDTO getDocumentationByUiId(User currentUser, String uiId) throws ApplicationException {
        // security start
        securityService.authzCanReadProject(currentUser, uiId);
        // security finish

        Project project = jdbcProjectRepo.getByUiIdForDocumentation(uiId);
        if (project == null) {
            throw new NotFoundException("Project");
        }
        return ProjectDocDTO.fromEntity(project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public ProjectReadDTO updateDocumentation(User currentUser, ProjectDocDTO documentationDTO)
            throws ApplicationException {
        // security start
        securityService.authzCanUpdateProject(currentUser, documentationDTO.id);
        // security finish

        Project project = projectRepo.get(documentationDTO.id);
        if (project == null) {
            throw new NotFoundException("Project");
        }
        project = checkRecordVersion(project, documentationDTO.recordVersion);

        String documentationMd = documentationDTO.documentation.strip();
        String documentationHtml = MarkdownUtils.markdownToHtml(documentationMd);
        project.setDocumentationMd(documentationMd);
        project.setDocumentationHtml(documentationHtml);

        project = projectRepo.merge(project);

        // statistics
        statisticsSender.send(new ProjectDocUpdateMessage(currentUser.getId()));
        // statistics

        return ProjectReadDTO.fromEntityWithDescription(project);
    }

    private Project getProjectByIdWithEverythingForReadAndFetchMembershipForSecurity(User currentUser, Long projectId) {
        if (currentUser == null) {
            return jdbcProjectRepo.getByIdWithEverythingForRead(projectId);
        }
        return jdbcProjectRepo.getByIdWithEverythingForReadAndFetchMembershipForSecurity(projectId, currentUser.getId());
    }

    private ProjectReadDTO getDTOWithPermissions(User currentUser, Long projectId, boolean includeApplication)
            throws ApplicationException {
        if (projectId == null) {
            throw new NotFoundException("Project");
        }
        Project project = getProjectByIdWithEverythingForReadAndFetchMembershipForSecurity(currentUser, projectId);
        if (project == null) {
            throw new NotFoundException("Project");
        }

        ProjectReadDTO dto = ProjectReadDTO.fromEntityWithDescriptionAndPermissions(project);
        if (includeApplication) {
            ProjectApplication application = projectApplicationRepo.getLastByUserAndProject(currentUser, project);
            dto.application = ProjectApplicationStatusDTO.fromEntity(application);
        }
        return dto;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ProjectReadDTO get(User currentUser, Long id, boolean includeApplication) throws ApplicationException {
        boolean canRead = false;
        try {
            // security start
            securityService.authzCanReadProject(currentUser, id);
            // security finish
            canRead = true;
        } finally {
            if (!canRead) {
                userFavoriteService.deleteByUserAndProjectId(currentUser, id);
            }
        }

        return getDTOWithPermissions(currentUser, id, includeApplication);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ProjectReadDTO getByUiId(User currentUser, String uiId, boolean includeApplication) throws ApplicationException {
        boolean canRead = false;
        Long id = null;
        try {
            // security start
            id = securityService.authzCanReadProject(currentUser, uiId);
            // security finish
            canRead = true;
        } finally {
            if (!canRead) {
                userFavoriteService.deleteByUserAndProjectUiId(currentUser, uiId);
            }
        }

        return getDTOWithPermissions(currentUser, id, includeApplication);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<ProjectNameAndUiIdDTO> listManagedByUser(User currentUser) {
        List<Project> projectList = projectRepo.listByNotFiredManagerProjectMemberUser(currentUser);
        return ProjectNameAndUiIdDTO.fromEntities(projectList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<ProjectNameAndUiIdDTO> listByUser(User currentUser) {
        List<Project> projectList = projectRepo.listByNotFiredProjectMemberUserAndOrderAscByName(currentUser);
        return ProjectNameAndUiIdDTO.fromEntities(projectList);
    }

    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<ProjectInviteOptionDTO> listInviteOptions(User currentUser, long userId) {
        List<Project> projects = projectRepo
                .listByNotFiredManagerProjectMemberUser(currentUser);

        List<Long> projectIds = new ArrayList<Long>();
        List<ProjectInviteOptionDTO> dtoList = new ArrayList<ProjectInviteOptionDTO>();
        for (Project project : projects) {
            ProjectInviteOptionDTO dto = ProjectInviteOptionDTO.fromEntity(project);
            projectIds.add(dto.id);
            dtoList.add(dto);
        }

        List<Tuple> projectIdsWithInviteStatuses = projectInviteRepo.listProjectIdAndInviteStatusByProjectIdsAndUserId(projectIds,
                userId);
        Map<Long, ProjectInvite> projectIdToInvite = new HashMap<Long, ProjectInvite>(projectIdsWithInviteStatuses.size());
        for (Tuple tuple : projectIdsWithInviteStatuses) {
            Long projectId = tuple.get(0, Long.class);
            ProjectInvite invite = tuple.get(1, ProjectInvite.class);
            invite.setStatus(tuple.get(2, InviteStatus.class));
            projectIdToInvite.put(projectId, invite); // invites are ordered ascending by time, so we get the last one
        }

        Set<Long> alreadyParticipatingProjectIds = new HashSet<Long>(projectRepo.listProjectIdsByNotFiredMemberUserId(userId));

        for (ProjectInviteOptionDTO dto : dtoList) {
            dto.isMember = alreadyParticipatingProjectIds.contains(dto.id);
            ProjectInvite invite = projectIdToInvite.get(dto.id);
            dto.invite = ProjectInviteStatusDTO.fromInvite(invite);
        }

        return dtoList;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.MANDATORY)
    public List<ProjectNameAndUiIdDTO> listAvailableToUser(User user) {
        if (user == null) {
            List<Project> projectList = projectRepo.listPublicAndOrderAscByName();
            return ProjectNameAndUiIdDTO.fromEntities(projectList);
        }

        // if (organizationMember.isManager()) {
        // List<Project> projectList = projectRepo.listAndOrderAscByName();
        // return ProjectNameAndUiIdDTO.fromEntities(projectList);
        // }

        List<Project> projectList = projectRepo.listAvailableToUserAndOrderAscByName(user);
        return ProjectNameAndUiIdDTO.fromEntities(projectList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ChatPermissionsDTO getChatToken(User currentUser, Long projectId) throws JsonProcessingException, Exception {
        // security
        securityService.authzCanReadProject(currentUser, projectId);
        // security

        ChatTokenData tokenData = new ChatTokenData();
        tokenData.chatType = ChatType.PROJECT;
        tokenData.chatId = projectId;

        if (currentUser == null) {
            tokenData.senderId = null;
            tokenData.write = false;
        } else {
            tokenData.senderId = currentUser.getId();
            try {
                securityService.authzCanWriteToProjectChat(currentUser, projectId);
                tokenData.write = true;
            } catch (SecurityException e) {
                tokenData.write = false;
            }
        }

        long now = System.currentTimeMillis();
        tokenData.notBefore = now - chatTokenNotBeforeToleranceSeconds * 1000;
        tokenData.notOnOrAfter = now + chatTokenLifetimeSeconds * 1000;

        TokenAndIvDTO dto = tokenGenerator.encrypt(tokenData.toJson());
        return new ChatPermissionsDTO(dto, true, tokenData.write);
    }
}
