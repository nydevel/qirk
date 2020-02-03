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
package org.wrkr.clb.services.security.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.ApplicationStatus;
import org.wrkr.clb.model.InviteStatus;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.model.project.ProjectInvite;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.Road;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskCard;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskHashtagToTaskMeta;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.ApplicationStatusRepo;
import org.wrkr.clb.repo.InviteStatusRepo;
import org.wrkr.clb.repo.organization.OrganizationRepo;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.project.task.TaskHashtagRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.security.ProjectSecurityService;

public class ProjectSecurityServiceTest extends BaseServiceTest {

    private static final String orgOwnerEmail = "org_owner@test.com";
    private static final String orgManagerEmail = "org_manager@test.com";
    private static final String projectManagerEmail = "project_manager@test.com";
    private static final String projectMemberEmail = "project_member@test.com";
    private static final String taskAssigneeEmail = "task_assignee@test.com";
    private static final String projectReadOnlyMemberEmail = "project_read_only_member@test.com";
    private static final String nonMemberEmail = "non_member@test.com";

    private static final String privateOrganizationName = "Private Organization";
    private static final String privateOrganizationUiId = "private_organization";

    private static final String publicOrganizationName = "Public Organization";
    private static final String publicOrganizationUiId = "public_organization";

    private static final String privateProjectName = "Private Project";
    private static final String privateProjectUiId = "private_project";

    private static final String publicProjectName = "Public Project";
    private static final String publicProjectUiId = "public_project";

    private static final String roadName = "road";

    private static final String taskCardName = "task_card";

    private static final String unusedTaskHashtagName = "unused_hashtag";
    private static final String usedTaskHashtagName = "used_hashtag";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private InviteStatusRepo inviteStatusRepo;

    @Autowired
    private ApplicationStatusRepo applicationStatusRepo;

    @Autowired
    private TaskHashtagRepo hashtagRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void beforeTest() throws Exception {
        User orgOwnerUser = saveUser(orgOwnerEmail);
        User orgManagerUser = saveUser(orgManagerEmail);
        User projectManagerUser = saveUser(projectManagerEmail);
        User projectMemberUser = saveUser(projectMemberEmail);
        User taskAssigneeUser = saveUser(taskAssigneeEmail);
        User projectReadOnlyMemberUser = saveUser(projectReadOnlyMemberEmail);
        User nonMemberUser = saveUser(nonMemberEmail);

        Organization privateOrganization = saveOrganization(orgOwnerUser, privateOrganizationName, privateOrganizationUiId, true);
        Organization publicOrganization = saveOrganization(orgOwnerUser, publicOrganizationName, publicOrganizationUiId, false);

        saveOrganizationMember(orgManagerUser, privateOrganization, true);
        OrganizationMember projectManager = saveOrganizationMember(projectManagerUser, privateOrganization, false);
        OrganizationMember projectMember = saveOrganizationMember(projectMemberUser, privateOrganization, false);
        OrganizationMember taskAssignee = saveOrganizationMember(taskAssigneeUser, privateOrganization, false);
        OrganizationMember projectReadOnlyMember = saveOrganizationMember(projectReadOnlyMemberUser, privateOrganization, false);

        Project privateProject = saveProject(privateOrganization, privateProjectName, privateProjectUiId, true);
        saveProject(publicOrganization, publicProjectName, publicProjectUiId, false);

        saveProjectInvite(nonMemberUser, privateProject, projectMemberUser,
                inviteStatusRepo.getByNameCode(InviteStatus.Status.PENDING));
        saveGrantedPermissionsProjectInvite(nonMemberUser, privateProject, projectMemberUser,
                inviteStatusRepo.getByNameCode(InviteStatus.Status.PENDING));
        saveProjectApplication(nonMemberUser, privateProject,
                applicationStatusRepo.getByNameCode(ApplicationStatus.Status.PENDING));

        saveProjectMember(projectManager, privateProject, true, true);
        saveProjectMember(projectMember, privateProject, false, true);
        saveProjectMember(taskAssignee, privateProject, false, false);
        saveProjectMember(projectReadOnlyMember, privateProject, false, false);

        Road road = saveRoad(privateProject, roadName);

        TaskCard card = saveTaskCard(road, taskCardName);

        Task task = createTask(projectMember, taskAssignee,
                testRepo.listEntities(TaskType.class).get(0),
                testRepo.listEntities(TaskPriority.class).get(0),
                testRepo.listEntities(TaskStatus.class).get(0),
                card);
        saveTasks(privateProject, Arrays.asList(task));

        saveIssue(privateProject, projectMemberUser);

        saveMemo(privateProject, projectMember);

        saveTaskHashtag(privateProject, unusedTaskHashtagName);
        TaskHashtag usedHashtag = saveTaskHashtag(privateProject, usedTaskHashtagName);
        hashtagRepo.setHashtagToTask(task.getId(), usedHashtag.getId());
    }

    @After
    public void afterTest() {
        jdbcTestRepo.clearTable(TaskHashtagToTaskMeta.TABLE_NAME);
        testRepo.clearTable(TaskHashtag.class);
        testRepo.clearTable(Memo.class);
        testRepo.clearTable(Issue.class);
        testRepo.clearTable(Task.class);
        testRepo.clearTable(TaskCard.class);
        testRepo.clearTable(Road.class);
        testRepo.clearTable(ProjectMember.class);
        testRepo.clearTable(ProjectApplication.class);
        testRepo.clearTable(GrantedPermissionsProjectInvite.class);
        testRepo.clearTable(ProjectInvite.class);
        testRepo.clearTable(Project.class);
        testRepo.clearTable(ProjectTaskNumberSequence.class);
        testRepo.clearTable(OrganizationMember.class);
        testRepo.clearTable(Organization.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_managerCanCreateProjectInTheirOrganizationById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);

        securityService.authzCanCreateProject(orgManager, new IdOrUiIdDTO(organization1.getId()));
    }

    @Test
    public void test_managerCanCreateProjectInTheirOrganizationByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanCreateProject(orgManager, new IdOrUiIdDTO(privateOrganizationUiId));
    }

    @Test
    public void test_orgManagerCanReadTheirProjectById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadProject(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanReadTheirProjectByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanReadProject(orgManager, privateProjectUiId);
    }

    @Test
    public void test_orgManagerCanUpdateTheirProject() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanUpdateProject(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanMakeTheirProjectPublic() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanMakeProjectPublic(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanAddTheirProjectToFavorite() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanAddProjectToFavorite(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectInvitesById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyProjectInvites(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectInvitesByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanModifyProjectInvites(orgManager, privateProjectUiId);
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectInvitesById2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyProjectInvites(orgManager, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectInvitesByUiId2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanModifyProjectInvites(orgManager, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectInvite() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        ProjectInvite invite = testRepo.listEntities(ProjectInvite.class).get(0);

        securityService.authzCanModifyProjectInvite(orgManager, invite.getId());
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectGrantedPermsInvite() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        GrantedPermissionsProjectInvite invite = testRepo.listEntities(GrantedPermissionsProjectInvite.class).get(0);

        securityService.authzCanModifyGrantedPermsInvite(orgManager, invite.getId());
    }

    @Test
    public void test_canApplyToPublicProject() {
        User nonMember = userRepo.getByEmail(nonMemberEmail);
        Project publicProject = projectRepo.getByUiId(publicProjectUiId);

        securityService.authzCanApplyToProject(nonMember, publicProject.getId());
    }

    @Test
    public void test_cantApplyToPrivateProject() {
        expectedException.expect(SecurityException.class);

        User nonMember = userRepo.getByEmail(nonMemberEmail);
        Project privateProject = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanApplyToProject(nonMember, privateProject.getId());
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectApplicationsById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyProjectApplications(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectApplicationsByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanModifyProjectApplications(orgManager, privateProjectUiId);
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectApplication() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        ProjectApplication application = testRepo.listEntities(ProjectApplication.class).get(0);

        securityService.authzCanModifyProjectApplication(orgManager, application.getId());
    }

    @Test
    public void test_orgManagerCanReadTheirProjectMembersById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadProjectMembers(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanReadTheirProjectMembersByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanReadProjectMembers(orgManager, privateProjectUiId);
    }

    @Test
    public void test_orgManagerCanReadTheirProjectMembersById2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadProjectMembers(orgManager, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_orgManagerCanReadTheirProjectMembersByUiId2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanReadProjectMembers(orgManager, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectMembers() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyProjectMembers(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanReadTheirProjectMember() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        User otherUser = userRepo.getByEmail(projectReadOnlyMemberEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);
        ProjectMember otherProjectMember = projectMemberRepo.getNotFiredByUserAndProject(otherUser, project1);

        securityService.authzCanReadProjectMember(orgManager, otherProjectMember.getId());
    }

    @Test
    public void test_orgManagerCanModifyTheirProjectMember() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        User otherUser = userRepo.getByEmail(projectReadOnlyMemberEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);
        ProjectMember otherProjectMember = projectMemberRepo.getNotFiredByUserAndProject(otherUser, project1);

        securityService.authzCanModifyProjectMember(orgManager, otherProjectMember.getId());
    }

    @Test
    public void test_orgManagerCanModifyRoadsInTheirProject() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyRoads(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanModifyRoadInTheirProject() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Road road = testRepo.listEntities(Road.class).get(0);

        securityService.authzCanModifyRoad(orgManager, road.getId());
    }

    @Test
    public void test_orgManagerCanModifyTaskCardsInTheirProjectByProjectId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyTaskCards(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanModifyTaskCardsInTheirProjectByRoadId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Road road = testRepo.listEntities(Road.class).get(0);

        securityService.authzCanModifyTaskCardsByRoadId(orgManager, road.getId());
    }

    @Test
    public void test_orgManagerCanModifyTaskCardInTheirProjectByCardId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        TaskCard card = testRepo.listEntities(TaskCard.class).get(0);

        securityService.authzCanModifyTaskCard(orgManager, card.getId());
    }

    @Test
    public void test_orgManagerCanModifyTaskCardInTheirProjectByTaskId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Task task = testRepo.listEntities(Task.class).get(0);

        securityService.authzCanModifyTaskCardByTaskId(orgManager, task.getId());
    }

    @Test
    public void test_orgManagerCanCreateTaskInTheirProjectById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanCreateTask(orgManager, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_orgManagerCanCreateTaskInTheirProjectByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanCreateTask(orgManager, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_orgManagerCanReadTheirProjectTasksById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadTasks(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanReadTheirProjectTasksById2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadTasks(orgManager, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_orgManagerCanReadTheirProjectTasksByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanReadTasks(orgManager, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_orgManagerCanReadTheirProjectTaskById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        securityService.authzCanReadTask(orgManager, task1.getId());
    }

    @Test
    public void test_orgManagerCanReadTheirProjectTaskByProjectIdAndNumber() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        Long taskId = securityService.authzCanReadTask(orgManager, new IdOrUiIdDTO(project1.getId()), task1.getNumber());

        assertEquals("task id doesn't match", task1.getId(), taskId);
    }

    @Test
    public void test_orgManagerCanReadTheirProjectTaskByProjectUiIdAndNumber() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        Long taskId = securityService.authzCanReadTask(orgManager, new IdOrUiIdDTO(privateProjectUiId), task1.getNumber());

        assertEquals("task id doesn't match", task1.getId(), taskId);
    }

    @Test
    public void test_orgManagerCanSubscribeToTheirProjectTaskById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        securityService.authzCanSubscribeToTask(orgManager, task1.getId());
    }

    @Test
    public void test_orgManagerCanUpdateTheirProjectTask() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        securityService.authzCanUpdateTask(orgManager, task1.getId());
    }

    @Test
    public void test_orgManagerCanCreateIssueInTheirProjectById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanCreateIssue(orgManager, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_orgManagerCanCreateIssueInTheirProjectByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanCreateIssue(orgManager, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_orgManagerCanReadTheirProjectIssuesById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadIssues(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanReadTheirProjectIssuesByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanReadIssues(orgManager, privateProjectUiId);
    }

    @Test
    public void test_orgManagerCanReadTheirProjectIssue() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Issue issue1 = testRepo.listEntities(Issue.class).get(0);

        securityService.authzCanReadIssue(orgManager, issue1.getId());
    }

    @Test
    public void test_orgManagerCanUpdateTheirProjectIssue() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Issue issue1 = testRepo.listEntities(Issue.class).get(0);

        securityService.authzCanUpdateIssue(orgManager, issue1.getId());
    }

    @Test
    public void test_orgManagerCanCreateMemoInTheirProjectById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanCreateMemo(orgManager, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_orgManagerCanCreateMemoInTheirProjectByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanCreateMemo(orgManager, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_orgManagerCanReadTheirProjectMemosById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadMemos(orgManager, project1.getId());
    }

    @Test
    public void test_orgManagerCanReadTheirProjectMemosByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        securityService.authzCanReadMemos(orgManager, privateProjectUiId);
    }

    @Test
    public void test_orgManagerCanDeleteTheirProjectMemo() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Memo memo1 = testRepo.listEntities(Memo.class).get(0);

        Long memoId = securityService.authzCanDeleteMemo(orgManager, memo1.getId());

        assertEquals("memo id doesn't match", memo1.getId(), memoId);
    }

    @Test
    public void test_orgManagerCanDeleteTheirProjectUnusedTaskHashtag() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project = projectRepo.getByUiId(privateProjectUiId);
        TaskHashtag unusedHashtag = hashtagRepo.getByProjectIdAndName(project.getId(), unusedTaskHashtagName);

        Long hashtagId = securityService.authzCanDeleteTaskHashtag(orgManager, unusedHashtag.getId());

        assertEquals("task hashtag id doesn't match", unusedHashtag.getId(), hashtagId);
    }

    @Test
    public void test_orgManagerCantDeleteTheirProjectUsedTaskHashtag() {
        expectedException.expect(SecurityException.class);

        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Project project = projectRepo.getByUiId(privateProjectUiId);
        TaskHashtag usedHashtag = hashtagRepo.getByProjectIdAndName(project.getId(), usedTaskHashtagName);

        securityService.authzCanDeleteTaskHashtag(orgManager, usedHashtag.getId());
    }
}
