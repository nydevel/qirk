package org.wrkr.clb.services.security.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.security.SecurityIssueRepo;
import org.wrkr.clb.repo.security.SecurityMemoRepo;
import org.wrkr.clb.repo.security.SecurityProjectRepo;
import org.wrkr.clb.repo.security.SecurityTaskRepo;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.util.exception.PaymentRequiredException;

public abstract class BaseProjectSecurityService extends BaseSecurityService {

    @Autowired
    protected SecurityProjectRepo projectRepo;

    @Autowired
    private SecurityTaskRepo taskRepo;

    @Autowired
    private SecurityIssueRepo issueRepo;

    @Autowired
    private SecurityMemoRepo memoRepo;

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(User user, Long projectId) {
        if (user == null) {
            return projectRepo.getByIdForSecurity(projectId);
        }
        return projectRepo.getByIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(projectId, user.getId());
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(User user, String projectUiId) {
        if (user == null) {
            return projectRepo.getByUiIdForSecurity(projectUiId);
        }
        return projectRepo.getByUiIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(projectUiId, user.getId());
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndProjectIdOrUiId(User user, IdOrUiIdDTO projectDTO) {
        if (projectDTO.id != null) {
            return getProjectWithOrgMemberAndProjectMemberByUserAndProjectId(user, projectDTO.id);
        }
        if (projectDTO.uiId != null) {
            return getProjectWithOrgMemberAndProjectMemberByUserAndProjectUiId(user, projectDTO.uiId);
        }
        return null;
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndInviteId(User user, Long inviteId) {
        if (user == null) {
            return projectRepo.getByInviteIdForSecurity(inviteId);
        }
        return projectRepo.getByInviteIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(inviteId, user.getId());
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndGrantedPermsInviteId(User user, Long inviteId) {
        if (user == null) {
            return projectRepo.getByGrantedPermsInviteIdForSecurity(inviteId);
        }
        return projectRepo.getByGrantedPermsInviteIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(inviteId,
                user.getId());
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndApplicationId(User user, Long applicationId) {
        if (user == null) {
            return projectRepo.getByApplicationIdForSecurity(applicationId);
        }
        return projectRepo.getByApplicationIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(applicationId,
                user.getId());
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndOtherMemberId(User user, Long projectMemberId) {
        if (user == null) {
            return projectRepo.getByMemberIdForSecurity(projectMemberId);
        }
        return projectRepo.getByOtherMemberIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(projectMemberId,
                user.getId());
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndRoadId(User user, Long roadId) {
        if (user == null) {
            return projectRepo.getByRoadIdForSecurity(roadId);
        }
        return projectRepo.getByRoadIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(roadId,
                user.getId());
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndCardId(User user, Long cardId) {
        if (user == null) {
            return projectRepo.getByCardIdForSecurity(cardId);
        }
        return projectRepo.getByCardIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(cardId,
                user.getId());
    }

    protected Project getProjectWithOrgMemberAndProjectMemberByUserAndTaskId(User user, Long taskId) {
        if (user == null) {
            return projectRepo.getByTaskIdForSecurity(taskId);
        }
        return projectRepo.getByTaskIdAndFetchNotFiredOrgMemberAndProjectMemberByUserIdForSecurity(taskId,
                user.getId());
    }

    protected void requirePaymentOrThrowException(@SuppressWarnings("unused") Project project) throws PaymentRequiredException {
        // uncomment when payment is on
        // if (project != null && project.isPrivate() && project.isFrozen()) {
        // throw new PaymentRequiredException("Payment required");
        // }
    }

    private void requireProjectMemberOrThrowException(ProjectMember member) throws SecurityException {
        if (member == null || member.isFired()) {
            throw new SecurityException("User is not a member of the project");
        }
    }

    private ProjectMember requireProjectMemberOrThrowException(List<ProjectMember> members, User user) {
        for (ProjectMember member : members) {
            if (member == null || !member.getUserId().equals(user.getId())) {
                continue;
            }
            requireProjectMemberOrThrowException(member);
            return member;
        }
        throw new SecurityException("User is not a member of the project");
    }

    private void requireProjectManagerOrThrowException(ProjectMember member) throws SecurityException {
        requireProjectMemberOrThrowException(member);
        if (!member.isManager()) {
            throw new SecurityException("User is not a manager of the project");
        }
    }

    protected ProjectMember requireProjectManagerOrThrowException(List<ProjectMember> members, User user) {
        for (ProjectMember member : members) {
            if (member == null || !member.getUserId().equals(user.getId())) {
                continue;
            }
            requireProjectManagerOrThrowException(member);
            return member;
        }
        throw new SecurityException("User is not a member of the project");
    }

    private void authzCanWriteToProject(ProjectMember member) throws SecurityException {
        requireProjectMemberOrThrowException(member);
        if (member.isManager()) {
            return; // gods can everything
        }
        if (!member.isWriteAllowed()) {
            throw new SecurityException("User can't write to the project");
        }
    }

    protected ProjectMember authzCanWriteToProject(List<ProjectMember> members, User user) throws SecurityException {
        for (ProjectMember member : members) {
            if (member == null || !member.getUserId().equals(user.getId())) {
                continue;
            }
            authzCanWriteToProject(member);
            return member;
        }
        throw new SecurityException("User is not a member of the project");
    }

    protected void authzCanReadProject(User user, Project project) throws SecurityException {
        if (project == null || !project.isPrivate()) {
            return; // public can be read by everyone
        }
        requireAuthnOrThrowException(user);

        List<ProjectMember> projectMembers = project.getMembers();
        requireProjectMemberOrThrowException(projectMembers, user);
    }

    protected void authzCanWriteToProject(User user, Project project) throws SecurityException {
        if (project == null) {
            return;
        }

        List<ProjectMember> projectMembers = project.getMembers();
        authzCanWriteToProject(projectMembers, user);
    }

    protected void authzCanWriteToProjectOrIsOrgMemberId(User user, Project project, List<Long> projectMemberIds)
            throws SecurityException {
        if (project == null) {
            return;
        }

        List<ProjectMember> projectMembers = project.getMembers();
        ProjectMember projectMember = authzCanWriteToProject(projectMembers, user);

        if (projectMemberIds.contains(projectMember.getId())) {
            return;
        }
    }

    protected void authzCanManageProject(User user, Project project) throws SecurityException {
        if (project == null) {
            return;
        }

        List<ProjectMember> projectMembers = project.getMembers();
        requireProjectManagerOrThrowException(projectMembers, user);
    }

    protected void authzCanManageProjectOrIsOrgMemberId(User user, Project project, List<Long> projectMemberIds)
            throws SecurityException {
        if (project == null) {
            return;
        }

        List<ProjectMember> projectMembers = project.getMembers();
        ProjectMember projectMember = requireProjectManagerOrThrowException(projectMembers, user);

        if (projectMemberIds.contains(projectMember.getId())) {
            return;
        }
    }

    protected Task getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskId(User user, Long taskId) {
        if (user == null) {
            return taskRepo.getByIdAndFetchProjectForSecurity(taskId);
        }
        return taskRepo.getByIdAndFetchProjectAndMembershipByUserIdForSecurity(taskId, user.getId());
    }

    private Task getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskNumberAndProjectId(User user, Long number,
            Long projectId) {
        if (user == null) {
            return taskRepo.getByNumberAndProjectIdAndFetchProjectForSecurity(number, projectId);
        }
        return taskRepo.getByNumberAndProjectIdAndFetchProjectAndMembershipByUserIdForSecurity(number, projectId,
                user.getId());
    }

    private Task getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskNumberAndProjectUiId(User user, Long number,
            String projectUiId) {
        if (user == null) {
            return taskRepo.getByNumberAndProjectUiIdAndFetchProjectForSecurity(number, projectUiId);
        }
        return taskRepo.getByNumberAndProjectUiIdAndFetchProjectAndMembershipByUserIdForSecurity(number, projectUiId,
                user.getId());
    }

    protected Task getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskNumberAndProject(User user, Long number,
            IdOrUiIdDTO projectDTO) {
        if (projectDTO.id != null) {
            return getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskNumberAndProjectId(user, number, projectDTO.id);
        }
        if (projectDTO.uiId != null) {
            return getTaskWithProjectAndOrgMemberAndProjectMemberByUserAndTaskNumberAndProjectUiId(user, number, projectDTO.uiId);
        }
        return null;
    }

    protected Issue getIssueWithProjectAndOrgMemberAndProjectMemberByUserAndIssueId(User user, Long issueId) {
        if (user == null) {
            return issueRepo.getByIdAndFetchProjectForSecurity(issueId);
        }
        return issueRepo.getByIdAndFetchProjectAndMembershipByUserIdForSecurity(issueId, user.getId());
    }

    protected Memo getMemoWithProjectAndOrgMemberAndProjectMemberByUserAndMemoId(User user, Long memoId) {
        if (user == null) {
            return memoRepo.getByIdAndFetchProjectForSecurity(memoId);
        }
        return memoRepo.getByIdAndFetchProjectAndMembershipByUserIdForSecurity(memoId, user.getId());
    }
}
