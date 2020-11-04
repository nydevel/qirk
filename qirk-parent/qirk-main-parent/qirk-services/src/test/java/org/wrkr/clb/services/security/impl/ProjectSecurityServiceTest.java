package org.wrkr.clb.services.security.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.project.ApplicationStatus;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.model.project.ProjectInvite;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.roadmap.Road;
import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskHashtagToTaskMeta;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.ApplicationStatusRepo;
import org.wrkr.clb.repo.project.InviteStatusRepo;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.project.task.TaskHashtagRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.security.ProjectSecurityService;

public class ProjectSecurityServiceTest extends BaseServiceTest {

    private static final String projectOwnerEmail = "project_owner@test.com";
    private static final String projectManagerEmail = "project_manager@test.com";
    private static final String projectMemberEmail = "project_member@test.com";
    private static final String taskAssigneeEmail = "task_assignee@test.com";
    private static final String projectReadOnlyMemberEmail = "project_read_only_member@test.com";
    private static final String nonMemberEmail = "non_member@test.com";

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
        User projectOwnerUser = saveUser(projectOwnerEmail, true);
        User projectManagerUser = saveUser(projectManagerEmail);
        User projectMemberUser = saveUser(projectMemberEmail);
        User taskAssigneeUser = saveUser(taskAssigneeEmail);
        User projectReadOnlyMemberUser = saveUser(projectReadOnlyMemberEmail);
        User nonMemberUser = saveUser(nonMemberEmail);

        Project privateProject = saveProject(projectOwnerUser, privateProjectName, privateProjectUiId, true);
        saveProject(projectOwnerUser, publicProjectName, publicProjectUiId, false);

        saveProjectInvite(nonMemberUser, privateProject, projectMemberUser,
                inviteStatusRepo.getByNameCode(InviteStatus.Status.PENDING));
        saveGrantedPermissionsProjectInvite(nonMemberUser, privateProject, projectMemberUser,
                inviteStatusRepo.getByNameCode(InviteStatus.Status.PENDING));
        saveProjectApplication(nonMemberUser, privateProject,
                applicationStatusRepo.getByNameCode(ApplicationStatus.Status.PENDING));

        saveProjectMember(projectManagerUser, privateProject, true, true);
        ProjectMember projectMember = saveProjectMember(projectMemberUser, privateProject, false, true);
        ProjectMember taskAssignee = saveProjectMember(taskAssigneeUser, privateProject, false, false);
        saveProjectMember(projectReadOnlyMemberUser, privateProject, false, false);

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
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_adminCanCreateProject() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanCreateProject(admin);
    }

    @Test
    public void test_adminCanImportProjects() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanImportProjects(admin);
    }

    @Test
    public void test_adminCanReadTheirProjectById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadProject(admin, project1.getId());
    }

    @Test
    public void test_adminCanReadTheirProjectByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanReadProject(admin, privateProjectUiId);
    }

    @Test
    public void test_adminCanUpdateTheirProject() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanUpdateProject(admin, project1.getId());
    }

    @Test
    public void test_adminCanMakeTheirProjectPublic() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanMakeProjectPublic(admin, project1.getId());
    }

    @Test
    public void test_adminCanAddTheirProjectToFavorite() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanAddProjectToFavorite(admin, project1.getId());
    }

    @Test
    public void test_adminCanModifyTheirProjectInvitesById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyProjectInvites(admin, project1.getId());
    }

    @Test
    public void test_adminCanModifyTheirProjectInvitesByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanModifyProjectInvites(admin, privateProjectUiId);
    }

    @Test
    public void test_adminCanModifyTheirProjectInvitesById2() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyProjectInvites(admin, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_adminCanModifyTheirProjectInvitesByUiId2() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanModifyProjectInvites(admin, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_adminCanModifyTheirProjectInvite() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        ProjectInvite invite = testRepo.listEntities(ProjectInvite.class).get(0);

        securityService.authzCanModifyProjectInvite(admin, invite.getId());
    }

    @Test
    public void test_adminCanModifyTheirProjectGrantedPermsInvite() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        GrantedPermissionsProjectInvite invite = testRepo.listEntities(GrantedPermissionsProjectInvite.class).get(0);

        securityService.authzCanModifyGrantedPermsInvite(admin, invite.getId());
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
    public void test_adminCanModifyTheirProjectApplicationsById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyProjectApplications(admin, project1.getId());
    }

    @Test
    public void test_adminCanModifyTheirProjectApplicationsByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanModifyProjectApplications(admin, privateProjectUiId);
    }

    // @Test
    // public void test_adminCanModifyTheirProjectApplication() {
    // User admin = userRepo.getByEmail(projectOwnerEmail);
    // ProjectApplication application = testRepo.listEntities(ProjectApplication.class).get(0);

    // securityService.authzCanModifyProjectApplication(admin, application.getId());
    // }

    @Test
    public void test_adminCanReadTheirProjectMembersById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadProjectMembers(admin, project1.getId());
    }

    @Test
    public void test_adminCanReadTheirProjectMembersByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanReadProjectMembers(admin, privateProjectUiId);
    }

    @Test
    public void test_adminCanReadTheirProjectMembersById2() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadProjectMembers(admin, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_adminCanReadTheirProjectMembersByUiId2() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanReadProjectMembers(admin, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_adminCanModifyTheirProjectMembers() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyProjectMembers(admin, project1.getId());
    }

    @Test
    public void test_adminCanReadTheirProjectMember() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        User otherUser = userRepo.getByEmail(projectReadOnlyMemberEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);
        ProjectMember otherProjectMember = projectMemberRepo.getNotFiredByUserAndProject(otherUser, project1);

        securityService.authzCanReadProjectMember(admin, otherProjectMember.getId());
    }

    @Test
    public void test_adminCanModifyTheirProjectMember() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        User otherUser = userRepo.getByEmail(projectReadOnlyMemberEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);
        ProjectMember otherProjectMember = projectMemberRepo.getNotFiredByUserAndProject(otherUser, project1);

        securityService.authzCanModifyProjectMember(admin, otherProjectMember.getId());
    }

    @Test
    public void test_adminCanModifyRoadsInTheirProject() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyRoads(admin, project1.getId());
    }

    @Test
    public void test_adminCanModifyRoadInTheirProject() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Road road = testRepo.listEntities(Road.class).get(0);

        securityService.authzCanModifyRoad(admin, road.getId());
    }

    @Test
    public void test_adminCanModifyTaskCardsInTheirProjectByProjectId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanModifyTaskCards(admin, project1.getId());
    }

    @Test
    public void test_adminCanModifyTaskCardsInTheirProjectByRoadId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Road road = testRepo.listEntities(Road.class).get(0);

        securityService.authzCanModifyTaskCardsByRoadId(admin, road.getId());
    }

    @Test
    public void test_adminCanModifyTaskCardInTheirProjectByCardId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        TaskCard card = testRepo.listEntities(TaskCard.class).get(0);

        securityService.authzCanModifyTaskCard(admin, card.getId());
    }

    @Test
    public void test_adminCanModifyTaskCardInTheirProjectByTaskId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Task task = testRepo.listEntities(Task.class).get(0);

        securityService.authzCanModifyTaskCardByTaskId(admin, task.getId());
    }

    @Test
    public void test_adminCanCreateTaskInTheirProjectById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanCreateTask(admin, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_adminCanCreateTaskInTheirProjectByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanCreateTask(admin, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_adminCanReadTheirProjectTasksById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadTasks(admin, project1.getId());
    }

    @Test
    public void test_adminCanReadTheirProjectTasksById2() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadTasks(admin, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_adminCanReadTheirProjectTasksByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanReadTasks(admin, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_adminCanReadTheirProjectTaskById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        securityService.authzCanReadTask(admin, task1.getId());
    }

    @Test
    public void test_adminCanReadTheirProjectTaskByProjectIdAndNumber() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        Long taskId = securityService.authzCanReadTask(admin, new IdOrUiIdDTO(project1.getId()), task1.getNumber());

        assertEquals("task id doesn't match", task1.getId(), taskId);
    }

    @Test
    public void test_adminCanReadTheirProjectTaskByProjectUiIdAndNumber() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        Long taskId = securityService.authzCanReadTask(admin, new IdOrUiIdDTO(privateProjectUiId), task1.getNumber());

        assertEquals("task id doesn't match", task1.getId(), taskId);
    }

    @Test
    public void test_adminCanSubscribeToTheirProjectTaskById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        securityService.authzCanSubscribeToTask(admin, task1.getId());
    }

    @Test
    public void test_adminCanUpdateTheirProjectTask() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Task task1 = testRepo.listEntities(Task.class).get(0);

        securityService.authzCanUpdateTask(admin, task1.getId());
    }

    @Test
    public void test_adminCanCreateIssueInTheirProjectById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanCreateIssue(admin, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_adminCanCreateIssueInTheirProjectByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanCreateIssue(admin, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_adminCanReadTheirProjectIssuesById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadIssues(admin, project1.getId());
    }

    @Test
    public void test_adminCanReadTheirProjectIssuesByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanReadIssues(admin, privateProjectUiId);
    }

    @Test
    public void test_adminCanReadTheirProjectIssue() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Issue issue1 = testRepo.listEntities(Issue.class).get(0);

        securityService.authzCanReadIssue(admin, issue1.getId());
    }

    @Test
    public void test_adminCanUpdateTheirProjectIssue() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Issue issue1 = testRepo.listEntities(Issue.class).get(0);

        securityService.authzCanUpdateIssue(admin, issue1.getId());
    }

    @Test
    public void test_adminCanCreateMemoInTheirProjectById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanCreateMemo(admin, new IdOrUiIdDTO(project1.getId()));
    }

    @Test
    public void test_adminCanCreateMemoInTheirProjectByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanCreateMemo(admin, new IdOrUiIdDTO(privateProjectUiId));
    }

    @Test
    public void test_adminCanReadTheirProjectMemosById() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Project project1 = projectRepo.getByUiId(privateProjectUiId);

        securityService.authzCanReadMemos(admin, project1.getId());
    }

    @Test
    public void test_adminCanReadTheirProjectMemosByUiId() {
        User admin = userRepo.getByEmail(projectOwnerEmail);

        securityService.authzCanReadMemos(admin, privateProjectUiId);
    }

    @Test
    public void test_adminCanDeleteTheirProjectMemo() {
        User admin = userRepo.getByEmail(projectOwnerEmail);
        Memo memo1 = testRepo.listEntities(Memo.class).get(0);

        Long memoId = securityService.authzCanDeleteMemo(admin, memo1.getId());

        assertEquals("memo id doesn't match", memo1.getId(), memoId);
    }

}
