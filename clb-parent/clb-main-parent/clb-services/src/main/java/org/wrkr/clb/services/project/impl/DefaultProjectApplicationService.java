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
package org.wrkr.clb.services.project.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.ApplicationStatus;
import org.wrkr.clb.model.ApplicationStatus.Status;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.ApplicationStatusRepo;
import org.wrkr.clb.repo.project.ProjectApplicationRepo;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberDTO;
import org.wrkr.clb.services.dto.project.ProjectApplicationDTO;
import org.wrkr.clb.services.dto.project.ProjectApplicationReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberPermissionsDTO;
import org.wrkr.clb.services.organization.OrganizationMemberService;
import org.wrkr.clb.services.project.ProjectApplicationService;
import org.wrkr.clb.services.project.ProjectMemberService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.NotFoundException;
import org.wrkr.clb.services.util.http.JsonStatusCode;


@Validated
@Service
public class DefaultProjectApplicationService implements ProjectApplicationService {

    @Autowired
    private ProjectApplicationRepo projectApplicationRepo;

    @Autowired
    private ApplicationStatusRepo applicationStatusRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private OrganizationMemberService organizationMemberService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private SecurityService authnSecurityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public ProjectApplicationReadDTO create(User currentUser, ProjectApplicationDTO projectApplicationDTO)
            throws ApplicationException {
        // security start
        securityService.authzCanApplyToProject(currentUser, projectApplicationDTO.projectId);
        // security finish

        Project project = projectRepo.getAndFetchOrganization(projectApplicationDTO.projectId);
        if (project == null) {
            throw new NotFoundException("Project");
        }

        ProjectMember member = projectMemberRepo.getNotFiredByUserAndProject(currentUser, project);
        if (member != null) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS,
                    "This user is already a member of this project.");
        }

        ProjectApplication application = projectApplicationRepo.getPendingByUserAndProject(currentUser, project);
        if (application != null) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS,
                    "This user already applied to this project.");
        }

        application = new ProjectApplication();
        application.setUser(currentUser);
        application.setProject(project);

        ApplicationStatus status = applicationStatusRepo.getByNameCode(Status.PENDING);
        application.setStatus(status);

        application.setText(projectApplicationDTO.text);

        OffsetDateTime now = DateTimeUtils.now();
        application.setCreatedAt(now);
        application.setUpdatedAt(now);

        projectApplicationRepo.persist(application);
        return ProjectApplicationReadDTO.fromEntity(application, true, true, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectApplicationReadDTO> listByProjectId(User currentUser, Long projectId) {
        // security start
        securityService.authzCanModifyProjectApplications(currentUser, projectId);
        // security finish

        List<ProjectApplication> applicationList = projectApplicationRepo.listPendingByProjectIdAndFetchUser(projectId);
        return ProjectApplicationReadDTO.fromEntitiesForProject(applicationList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectApplicationReadDTO> listByProjectUiId(User currentUser, String projectUiId) {
        // security start
        securityService.authzCanModifyProjectApplications(currentUser, projectUiId);
        // security finish

        List<ProjectApplication> applicationList = projectApplicationRepo.listPendingByProjectUiIdAndFetchUser(projectUiId);
        return ProjectApplicationReadDTO.fromEntitiesForProject(applicationList);
    }

    @Override
    @Transactional()
    public ProjectApplicationReadDTO reject(User currentUser, RejectDTO rejectDTO) throws ApplicationException {
        // security start
        securityService.authzCanModifyProjectApplication(currentUser, rejectDTO.id);
        // security finish

        ProjectApplication application = projectApplicationRepo.getPendingById(rejectDTO.id, false);
        if (application == null) {
            throw new NotFoundException("Project application");
        }
        application.setStatus(applicationStatusRepo.getByNameCode(Status.REJECTED));
        application.setReported(rejectDTO.reported);
        application.setUpdatedAt(DateTimeUtils.now());

        application = projectApplicationRepo.merge(application);
        return ProjectApplicationReadDTO.fromEntity(application);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void accept(User currentUser, ProjectMemberPermissionsDTO applicationDTO) throws Exception {
        // security start
        securityService.authzCanModifyProjectApplication(currentUser, applicationDTO.id);
        // security finish

        ProjectApplication application = projectApplicationRepo.getPendingById(applicationDTO.id, true);
        if (application == null) {
            throw new NotFoundException("Project application");
        }

        OrganizationMember organizationMember = organizationMemberService.create(
                application.getUser(), application.getProject().getOrganization(), new OrganizationMemberDTO(), true);
        projectMemberService.create(application.getProject(), organizationMember,
                new ProjectMemberDTO(applicationDTO.writeAllowed, applicationDTO.manager));

        projectApplicationRepo.delete(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectApplicationReadDTO> listByUser(User currentUser) {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        List<ProjectApplication> applicationList = projectApplicationRepo
                .listByUserAndFetchProjectAndOrganizationAndStatus(currentUser);
        return ProjectApplicationReadDTO.fromEntitiesForUser(applicationList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void cancel(User currentUser, Long id) throws ApplicationException {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        ProjectApplication application = projectApplicationRepo.getByIdAndUser(id, currentUser);
        if (application == null) {
            throw new NotFoundException("Project application");
        }

        projectApplicationRepo.delete(application);
    }
}
