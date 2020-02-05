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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.TokenGenerator;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;
import org.wrkr.clb.common.util.chat.ChatType;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.IssueRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.project.IssueDTO;
import org.wrkr.clb.services.dto.project.IssueReadDTO;
import org.wrkr.clb.services.project.IssueService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;

//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultIssueService implements IssueService {

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

    public void setChatTokenLifetimeMinutes(Integer chatTokenLifetimeSeconds) {
        this.chatTokenLifetimeSeconds = chatTokenLifetimeSeconds;
    }

    @Autowired
    private IssueRepo issueRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public IssueReadDTO create(User currentUser, IssueDTO issueDTO) throws ApplicationException {
        // security start
        securityService.authzCanCreateIssue(currentUser, issueDTO.project);
        // security finish

        Issue issue = new Issue();
        issue.setSummary(issueDTO.summary.strip());
        issue.setDescription(issueDTO.description.strip());

        Project project = null;
        if (issueDTO.project.id != null) {
            project = projectRepo.get(issueDTO.project.id);
        } else if (issueDTO.project.uiId != null) {
            project = projectRepo.getByUiId(issueDTO.project.uiId.strip());
        }
        if (project == null) {
            throw new NotFoundException("Project");
        }
        issue.setProject(project);

        issue.setReporter(currentUser);

        OffsetDateTime now = DateTimeUtils.now();
        issue.setCreatedAt(now);

        issueRepo.persist(issue);
        return IssueReadDTO.fromEntity(issue);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public IssueReadDTO update(User currentUser, IssueDTO issueDTO) throws ApplicationException {
        // security start
        securityService.authzCanUpdateIssue(currentUser, issueDTO.id);
        // security finish

        Issue issue = issueRepo.getAndFetchUser(issueDTO.id);
        if (issue == null) {
            throw new NotFoundException("Issue");
        }

        issue.setSummary(issueDTO.summary.strip());
        issue.setDescription(issueDTO.description.strip());

        issue = issueRepo.merge(issue);
        return IssueReadDTO.fromEntity(issue);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public IssueReadDTO get(User currentUser, Long issueId) throws Exception {
        // security start
        securityService.authzCanReadIssue(currentUser, issueId);
        // security finish

        Issue issue = issueRepo.getAndFetchUser(issueId);
        return IssueReadDTO.fromEntity(issue);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<IssueReadDTO> listByProjectId(User currentUser, Long projectId) {
        // security start
        securityService.authzCanReadIssues(currentUser, projectId);
        // security finish

        List<Issue> issueList = issueRepo.listByProjectIdAndFetchReporter(projectId);
        return IssueReadDTO.fromEntities(issueList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<IssueReadDTO> listByProjectUiId(User currentUser, String projectUiId) {
        // security start
        securityService.authzCanReadIssues(currentUser, projectUiId);
        // security finish

        List<Issue> issueList = issueRepo.listByProjectUiIdAndFetchReporter(projectUiId);
        return IssueReadDTO.fromEntities(issueList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ChatPermissionsDTO getChatToken(User currentUser, Long issueId) throws Exception {
        // security
        securityService.authzCanReadIssue(currentUser, issueId);
        // security

        ChatTokenData tokenData = new ChatTokenData();
        tokenData.chatType = ChatType.ISSUE;
        tokenData.chatId = issueId;

        if (currentUser == null) {
            tokenData.senderId = null;
            tokenData.write = false;
        } else {
            tokenData.senderId = currentUser.getId();
            tokenData.write = true;
        }

        long now = System.currentTimeMillis();
        tokenData.notBefore = now - chatTokenNotBeforeToleranceSeconds * 1000;
        tokenData.notOnOrAfter = now + chatTokenLifetimeSeconds * 1000;

        TokenAndIvDTO dto = tokenGenerator.encrypt(tokenData.toJson());
        return new ChatPermissionsDTO(dto, true, tokenData.write);
    }

}
