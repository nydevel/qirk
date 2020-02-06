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
package org.wrkr.clb.services.security;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;

@Validated
public interface ProjectSecurityService {

    public void authzCanCreateProject(User user) throws SecurityException;

    public void authzCanImportProjects(User user) throws SecurityException;
    
    public void authzCanReadProject(User user,
            @NotNull(message = "projectId is null") Long projectId) throws SecurityException;

    public Long authzCanReadProject(User user,
            @NotNull(message = "projectUiId is null") String projectUiId) throws SecurityException;

    public void authzCanUpdateProject(User user,
            @NotNull(message = "projectId is null") Long projectId) throws SecurityException;

    public void authzCanMakeProjectPublic(User user,
            @NotNull(message = "projectId is null") Long projectId) throws SecurityException;

    public void authzCanWriteToProjectChat(User user,
            @NotNull(message = "projectId is null") Long projectId) throws SecurityException;

    public void authzCanAddProjectToFavorite(User user,
            @NotNull(message = "projectId must not be null") Long projectId) throws SecurityException;

    public void authzCanModifyProjectInvites(User user,
            @NotNull(message = "projectId must not be null") Long projectId) throws SecurityException;

    public void authzCanModifyProjectInvites(User user,
            @NotNull(message = "projectUiId must not be null") String projectUiId) throws SecurityException;

    public void authzCanModifyProjectInvites(User user, @Valid IdOrUiIdDTO projectDTO) throws SecurityException;

    public void authzCanModifyProjectInvite(User user,
            @NotNull(message = "inviteId must not be null") Long inviteId) throws SecurityException;

    public void authzCanModifyGrantedPermsInvite(User user,
            @NotNull(message = "inviteId must not be null") Long inviteId) throws SecurityException;

    public void authzCanApplyToProject(User user,
            @NotNull(message = "projectId must not be null") Long projectId) throws SecurityException;

    public void authzCanModifyProjectApplications(User user,
            @NotNull(message = "projectId must not be null") Long projectId) throws SecurityException;

    public void authzCanModifyProjectApplications(User user,
            @NotNull(message = "projectUiId must not be null") String projectUiId) throws SecurityException;

    public void authzCanModifyProjectApplication(User user,
            @NotNull(message = "applicationId must not be null") Long applicationId) throws SecurityException;

    public void authzCanReadProjectMembers(User user,
            @NotNull(message = "projectId must not be null") Long projectId) throws SecurityException;

    public void authzCanReadProjectMembers(User user,
            @NotNull(message = "projectUiId must not be null") String projectUiId) throws SecurityException;

    public void authzCanReadProjectMembers(User currentUser, @Valid IdOrUiIdDTO projectDTO) throws SecurityException;

    public void authzCanReadProjectMember(User user,
            @NotNull(message = "projectMemberId must not be null") Long projectMemberId) throws SecurityException;

    public void authzCanModifyProjectMembers(User user,
            @NotNull(message = "projectId must not be null") Long projectId) throws SecurityException;

    public void authzCanModifyProjectMember(User user,
            @NotNull(message = "projectMemberId must not be null") Long projectMemberId) throws SecurityException;

    public void authzCanModifyRoads(User user,
            @NotNull(message = "projectId is null") Long projectId) throws SecurityException;

    public void authzCanModifyRoads(User user,
            @NotNull(message = "projectUiId is null") String projectUiId) throws SecurityException;

    public void authzCanModifyRoad(User user,
            @NotNull(message = "roadId is null") Long roadId) throws SecurityException;

    public void authzCanModifyTaskCards(User user,
            @NotNull(message = "projectId is null") Long projectId) throws SecurityException;

    public void authzCanModifyTaskCards(User user,
            @NotNull(message = "projectUiId is null") String projectUiId) throws SecurityException;

    public void authzCanModifyTaskCardsByRoadId(User user,
            @NotNull(message = "roadId is null") Long roadId) throws SecurityException;

    public void authzCanModifyTaskCard(User user,
            @NotNull(message = "cardId is null") Long cardId) throws SecurityException;

    public void authzCanModifyTaskCardByTaskId(User user,
            @NotNull(message = "taskId is null") Long taskId) throws SecurityException;

    public boolean authzCanCreateTaskNoException(User user,
            @NotNull(message = "project must not be null") Project project);

    public void authzCanCreateTask(User user,
            @NotNull(message = "projectId is null") Long projectId) throws SecurityException;

    public void authzCanCreateTask(User user,
            @Valid IdOrUiIdDTO projectDTO) throws SecurityException;

    public void authzCanReadTasks(User user,
            @NotNull(message = "projectId is null") Long projectId) throws SecurityException;

    public void authzCanReadTasks(User user, @Valid IdOrUiIdDTO projectDTO) throws SecurityException;

    public void authzCanReadTask(User user,
            @NotNull(message = "taskId is null") Long taskId) throws SecurityException;

    public Long authzCanReadTask(User user, @Valid IdOrUiIdDTO projectDTO,
            @NotNull(message = "number must not be null") Long number) throws SecurityException;

    public void authzCanSubscribeToTask(User user,
            @NotNull(message = "taskId is null") Long taskId) throws SecurityException;

    public void authzCanUpdateTask(User user,
            @NotNull(message = "taskId is null") Long taskId) throws SecurityException;

    public void authzCanCreateIssue(User user,
            @Valid IdOrUiIdDTO projectDTO) throws SecurityException;

    public void authzCanReadIssues(User user,
            @NotNull(message = "projectId must not be null") Long projectId) throws SecurityException;

    public void authzCanReadIssues(User user,
            @NotNull(message = "projectUiId must not be null") String projectUiId) throws SecurityException;

    public void authzCanReadIssue(User user,
            @NotNull(message = "issueId must not be null") Long issueId) throws SecurityException;

    public void authzCanUpdateIssue(User user,
            @NotNull(message = "issueId must not be null") Long issueId) throws SecurityException;

    public void authzCanCreateMemo(User user,
            @Valid IdOrUiIdDTO projectDTO) throws SecurityException;

    public void authzCanReadMemos(User user,
            @NotNull(message = "projectId must not be null") Long projectId) throws SecurityException;

    public void authzCanReadMemos(User user,
            @NotNull(message = "projectUiId must not be null") String projectUiId) throws SecurityException;

    public Long authzCanDeleteMemo(User user,
            @NotNull(message = "memoId must not be null") Long memoId) throws SecurityException;

    public Long authzCanDeleteTaskHashtag(User user,
            @NotNull(message = "hashtagId must not be null") Long hashtagId) throws SecurityException;
}
