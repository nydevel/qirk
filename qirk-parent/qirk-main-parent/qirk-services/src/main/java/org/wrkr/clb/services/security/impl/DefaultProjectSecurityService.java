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
package org.wrkr.clb.services.security.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.security.ProjectSecurityService;

@Validated
@Service
public class DefaultProjectSecurityService extends BaseProjectSecurityService implements ProjectSecurityService {

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanCreateProject(User user, IdOrUiIdDTO organizationDTO) throws SecurityException {
        requireAuthnOrThrowException(user);

        OrganizationMember member = getOrganizationMemberByUserAndOrganizationIdOrUiId(user, organizationDTO);
        requireOrganizationManagerOrThrowException(member);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadProject(User user, Long projectId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanReadProject(user, project);
    }

    /**
     * 
     * @return id of the project; null if it does not exist
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Long authzCanReadProject(User user, String projectUiId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(user, projectUiId);
        authzCanReadProject(user, project);
        return (project == null ? null : project.getId());
    }

    private void authzCanUpdateProject(User user, Project project) throws SecurityException {
        requirePaymentOrThrowException(project);
        authzCanManageProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanUpdateProject(User user, Long projectId) throws SecurityException {
        requireAuthnOrThrowException(user);
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanUpdateProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanMakeProjectPublic(User user, Long projectId) throws SecurityException {
        requireAuthnOrThrowException(user);
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanManageProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanWriteToProjectChat(User user, Long projectId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        List<OrganizationMember> orgMembers = project.getOrganizationMembers();
        OrganizationMember orgMember = requireOrganizationMemberOrThrowException(orgMembers, user);

        if (orgMember.isManager()) {
            return; // gods can everything
        }

        List<ProjectMember> projectMembers = orgMember.getProjectMembership();
        authzCanWriteToProject(projectMembers, user);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanAddProjectToFavorite(User user, Long projectId)
            throws SecurityException {
        requireAuthnOrThrowException(user);

        authzCanReadProject(user, projectId);
    }

    private void authzCanModifyProjectInvites(User user, Project project) throws SecurityException {
        authzCanManageProject(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectInvites(User user, Long projectId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanModifyProjectInvites(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectInvites(User user, String projectUiId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(user, projectUiId);
        authzCanModifyProjectInvites(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectInvites(User user, IdOrUiIdDTO projectDTO) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectIdOrUiId(user, projectDTO);
        authzCanModifyProjectInvites(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectInvite(User user, Long inviteId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndInviteId(user, inviteId);
        authzCanModifyProjectInvites(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyGrantedPermsInvite(User user, Long inviteId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndGrantedPermsInviteId(user, inviteId);
        authzCanModifyProjectInvites(user, project);
    }

    private void authzCanModifyProjectApplications(User user, Project project) throws SecurityException {
        authzCanManageProject(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectApplications(User user, Long projectId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanModifyProjectApplications(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectApplications(User user, String projectUiId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(user, projectUiId);
        authzCanModifyProjectApplications(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectApplication(User user, Long applicationId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndApplicationId(user, applicationId);
        authzCanModifyProjectApplications(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanApplyToProject(User user, Long projectId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = projectRepo.getByIdForSecurity(projectId);
        if (project == null || !project.isPrivate()) {
            return; // public can be applied to by everyone
        }
        throw new SecurityException("Project is private");
    }

    private void authzCanReadProjectMembers(User user, Project project) throws SecurityException {
        authzCanReadProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadProjectMembers(User user, Long projectId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanReadProjectMembers(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadProjectMembers(User user, String projectUiId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(user, projectUiId);
        authzCanReadProjectMembers(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadProjectMembers(User user, IdOrUiIdDTO projectDTO) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectIdOrUiId(user, projectDTO);
        authzCanReadProjectMembers(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadProjectMember(User user, Long projectMemberId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndOtherMemberId(user, projectMemberId);
        authzCanReadProjectMembers(user, project);
    }

    private void authzCanModifyProjectMembers(User user, Project project) throws SecurityException {
        authzCanManageProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectMembers(User user, Long projectId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanModifyProjectMembers(user, project);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyProjectMember(User user, Long projectMemberId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndOtherMemberId(user, projectMemberId);
        authzCanModifyProjectMembers(user, project);
    }

    private void authzCanModifyRoadsAndTaskCards(User user, Project project) throws SecurityException {
        requirePaymentOrThrowException(project);
        authzCanManageProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyRoads(User user, Long projectId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanModifyRoadsAndTaskCards(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyRoads(User user, String projectUiId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(user, projectUiId);
        authzCanModifyRoadsAndTaskCards(user, project);
    }

    /**
     * 
     * @return id of the road; null if it does not exist
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyRoad(User user, Long roadId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndRoadId(user, roadId);
        authzCanModifyRoadsAndTaskCards(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyTaskCards(User user, Long projectId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanModifyRoadsAndTaskCards(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyTaskCards(User user, String projectUiId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(user, projectUiId);
        authzCanModifyRoadsAndTaskCards(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyTaskCardsByRoadId(User user, Long roadId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndRoadId(user, roadId);
        authzCanModifyRoadsAndTaskCards(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyTaskCard(User user, Long cardId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndCardId(user, cardId);
        authzCanModifyRoadsAndTaskCards(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyTaskCardByTaskId(User user, Long taskId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndTaskId(user, taskId);
        authzCanModifyRoadsAndTaskCards(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public boolean authzCanCreateTaskNoException(User user, Project project) {
        if (!_isAuthenticated(user)) {
            return false;
        }

        // uncomment when payment is on
        // if (project.isPrivate() && project.isFrozen()) {
        // return false;
        // }

        List<OrganizationMember> orgMembers = project.getOrganizationMembers();
        for (OrganizationMember orgMember : orgMembers) {
            if (orgMember == null || !orgMember.getUserId().equals(user.getId())) {
                continue;
            }

            if (orgMember.isManager()) {
                return true;
            }

            List<ProjectMember> projectMembers = orgMember.getProjectMembership();
            for (ProjectMember projectMember : projectMembers) {
                if (projectMember == null || !projectMember.getUserId().equals(user.getId())) {
                    continue;
                }
                return (projectMember.isWriteAllowed() || projectMember.isManager());
            }
        }

        return false;
    }

    private void authzCanCreateTask(User user, Project project) throws SecurityException {
        requirePaymentOrThrowException(project);
        authzCanWriteToProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanCreateTask(User user, Long projectId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanCreateTask(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanCreateTask(User user, IdOrUiIdDTO projectDTO) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectIdOrUiId(user, projectDTO);
        authzCanCreateTask(user, project);
    }

    private void authzCanReadTasks(User user, Project project) throws SecurityException {
        authzCanReadProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadTasks(User user, Long projectId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanReadTasks(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadTasks(User user, IdOrUiIdDTO projectDTO) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectIdOrUiId(user, projectDTO);
        authzCanReadTasks(user, project);
    }

    private void authzCanUpdateTasks(User user, Project project) throws SecurityException {
        requirePaymentOrThrowException(project);
        authzCanWriteToProject(user, project);
    }

    private void authzCanReadTask(User user, Task task) throws SecurityException {
        if (task == null) {
            return;
        }

        /*@formatter:off
        if (_isAuthenticated(user)) {
            List<OrganizationMember> members = task.getProject().getOrganizationMembers();

            for (OrganizationMember member : members) {
                if (member.getUserId().equals(user.getId()) && isTaskReporterOrAssignee(task, member)) {
                    return; // reporter or assignee of the task
                }
            }
        }
        @formatter:on*/

        authzCanReadTasks(user, task.getProject());
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadTask(User user, Long taskId) throws SecurityException {
        Task task = getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskId(user, taskId);
        authzCanReadTask(user, task);
    }

    /**
     * 
     * @return id of the task; null if it does not exist
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Long authzCanReadTask(User user, IdOrUiIdDTO projectDTO, Long number) throws SecurityException {
        Task task = getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskNumberAndProject(user, number, projectDTO);
        authzCanReadTask(user, task);
        return (task == null ? null : task.getId());
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanSubscribeToTask(User user, Long taskId) throws SecurityException {
        requireAuthnOrThrowException(user);
        Task task = getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskId(user, taskId);
        authzCanReadTask(user, task);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanUpdateTask(User user, Long taskId) throws SecurityException {
        requireAuthnOrThrowException(user);
        Task task = getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskId(user, taskId);
        if (task == null) {
            return;
        }

        authzCanUpdateTasks(user, task.getProject());
        /*@formatter:off
        requirePaymentOrThrowException(project);
        authzCanWriteToProjectOrIsOrgMemberId(user, task.getProject(), Arrays.asList(task.getReporterId(), task.getAssigneeId()));
        @formatter:on*/
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanCreateIssue(User user, IdOrUiIdDTO projectDTO) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectIdOrUiId(user, projectDTO);
        requirePaymentOrThrowException(project);
        authzCanReadProject(user, project);
    }

    private void authzCanReadIssues(User user, Project project) throws SecurityException {
        authzCanReadProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadIssues(User user, Long projectId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectId);
        authzCanReadIssues(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadIssues(User user, String projectUiId) throws SecurityException {
        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(user, projectUiId);
        authzCanReadIssues(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadIssue(User user, Long issueId) throws SecurityException {
        Issue issue = getIssueWithProjectAndOrgMemberAndProjectMemberByUserAndIssueId(user, issueId);
        if (issue == null) {
            return;
        }

        if (_isAuthenticated(user)) {
            if (issue.getReporterId().equals(user.getId())) {
                return; // reporter of the issue
            }
        }

        authzCanReadIssues(user, issue.getProject());
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanUpdateIssue(User user, Long issueId) throws SecurityException {
        Issue issue = getIssueWithProjectAndOrgMemberAndProjectMemberByUserAndIssueId(user, issueId);
        if (issue == null) {
            return;
        }

        requirePaymentOrThrowException(issue.getProject());
        if (issue.getReporterId().equals(user.getId())) {
            return; // reporter of the issue
        }
        authzCanWriteToProject(user, issue.getProject());
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanCreateMemo(User user, IdOrUiIdDTO projectDTO) throws SecurityException {
        requireAuthnOrThrowException(user);

        Project project = getProjectWithOrgMemberAndProjectMemberByUserAndProjectIdOrUiId(user, projectDTO);
        requirePaymentOrThrowException(project);
        authzCanWriteToProject(user, project);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadMemos(User user, Long projectId) throws SecurityException {
        authzCanReadProject(user, projectId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadMemos(User user, String projectUiId) throws SecurityException {
        authzCanReadProject(user, projectUiId);
    }

    /**
     * 
     * @return id of the memo; null if it does not exist
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Long authzCanDeleteMemo(User user, Long memoId) throws SecurityException {
        requireAuthnOrThrowException(user);
        Memo memo = getMemoWithProjectAndOrgMemberAndProjectMemberByUserAndMemoId(user, memoId);
        if (memo == null) {
            return null;
        }

        requirePaymentOrThrowException(memo.getProject());
        authzCanManageProjectOrIsOrgMemberId(user, memo.getProject(), Arrays.asList(memo.getAuthorId()));
        return memo.getId();
    }

    /**
     * 
     * @return id of the task hashtag; null if it does not exist
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Long authzCanDeleteTaskHashtag(User user, Long hashtagId)
            throws SecurityException {
        requireAuthnOrThrowException(user);
        if (taskHashtagRepo.existsTaskIdByTaskHashtagId(hashtagId)) {
            throw new SecurityException("Task hashtag is used");
        }
        TaskHashtag hashtag = getTaskHashtagWithProjectAndOrgMemberAndProjectMemberByUserAndMemoId(user, hashtagId);
        if (hashtag == null) {
            return null;
        }

        requirePaymentOrThrowException(hashtag.getProject());
        authzCanManageProject(user, hashtag.getProject());
        return hashtag.getId();
    }
}
