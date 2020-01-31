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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.jms.statistics.NewMemoMessage;
import org.wrkr.clb.common.jms.statistics.StatisticsSender;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.organization.OrganizationMemberRepo;
import org.wrkr.clb.repo.project.MemoRepo;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.services.dto.project.MemoDTO;
import org.wrkr.clb.services.dto.project.MemoReadDTO;
import org.wrkr.clb.services.project.MemoService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;


@Validated
@Service
public class DefaultMemoService implements MemoService {

    @Autowired
    private MemoRepo memoRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private OrganizationMemberRepo organizationMemberRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private StatisticsSender statisticsSender;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public MemoReadDTO create(User currentUser, MemoDTO memoDTO) throws ApplicationException {
        // security start
        securityService.authzCanCreateMemo(currentUser, memoDTO.project);
        // security finish

        Project project = null;
        if (memoDTO.project.id != null) {
            project = projectRepo.getAndFetchOrganization(memoDTO.project.id);
        } else if (memoDTO.project.uiId != null) {
            project = projectRepo.getByUiIdAndFetchOrganization(memoDTO.project.uiId.strip());
        }
        if (project == null) {
            throw new NotFoundException("Project");
        }

        Memo memo = new Memo();
        memo.setProject(project);
        memo.setBody(memoDTO.body.strip());

        OrganizationMember author = organizationMemberRepo.getNotFiredByUserAndOrganizationId(
                currentUser, project.getOrganization().getId());
        memo.setAuthor(author);

        memo.setCreatedAt(DateTimeUtils.now());
        memoRepo.persist(memo);

        // statistics
        statisticsSender.send(new NewMemoMessage(currentUser.getId(), memo.getCreatedAt().toInstant().toEpochMilli()));
        // statistics

        return MemoReadDTO.fromEntity(memo, true);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<MemoReadDTO> listByProjectId(User currentUser, Long projectId) {
        // security start
        securityService.authzCanReadMemos(currentUser, projectId);
        // security finish

        List<Memo> memoList = memoRepo.listByProjectIdAndFetchAuthor(projectId);

        OrganizationMember currentOrganizationMember = null;
        ProjectMember currentProjectMember = null;
        if (currentUser != null) {
            currentOrganizationMember = organizationMemberRepo.getNotFiredByUserAndProjectId(currentUser, projectId);
            if (currentOrganizationMember == null || !currentOrganizationMember.isManager()) {
                currentProjectMember = projectMemberRepo.getNotFiredByOrganizationMemberAndProjectId(currentOrganizationMember,
                        projectId);
            }
        }
        return MemoReadDTO.fromEntities(memoList, currentOrganizationMember, currentProjectMember);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<MemoReadDTO> listByProjectUiId(User currentUser, String projectUiId) {
        // security start
        securityService.authzCanReadMemos(currentUser, projectUiId);
        // security finish

        List<Memo> memoList = memoRepo.listByProjectUiIdAndFetchAuthor(projectUiId);

        OrganizationMember currentOrganizationMember = null;
        ProjectMember currentProjectMember = null;
        if (currentUser != null) {
            currentOrganizationMember = organizationMemberRepo.getNotFiredByUserAndProjectUiId(currentUser, projectUiId);
            if (currentOrganizationMember == null || !currentOrganizationMember.isManager()) {
                currentProjectMember = projectMemberRepo.getNotFiredByOrganizationMemberAndProjectUiId(currentOrganizationMember,
                        projectUiId);
            }
        }
        return MemoReadDTO.fromEntities(memoList, currentOrganizationMember, currentProjectMember);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void delete(User currentUser, Long id) throws ApplicationException {
        // security start
        Long memoId = securityService.authzCanDeleteMemo(currentUser, id);
        // security finish

        if (memoId == null) {
            throw new NotFoundException("Memo");
        }
        memoRepo.deleteById(memoId);
    }

}
