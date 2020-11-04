package org.wrkr.clb.services.project.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.project.ProjectMemberService;

public class ProjectMemberServiceTest extends BaseServiceTest {

    private static final String userPassword = "password";
    private static final String projectOwnerEmail = "org_owner@test.com";
    private static final String projectManagerEmail = "project_manager@test.com";
    private static final String projectMemberEmail = "project_member@test.com";
    private static final String taskAssigneeEmail = "task_assignee@test.com";
    private static final String projectReadOnlyMemberEmail = "project_read_only_member@test.com";
    private static final String nonMemberEmail = "non_member@test.com";

    private static final String privateProjectName = "Private Project";
    private static final String privateProjectUiId = "private_project";

    private static final String publicProjectName = "Public Project";
    private static final String publicProjectUiId = "public_project";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void beforeTest() throws Exception {
        User projectOwnerUser = saveUser(projectOwnerEmail, userPassword);
        User projectManagerUser = saveUser(projectManagerEmail, userPassword);
        User projectMemberUser = saveUser(projectMemberEmail, userPassword);
        User taskAssigneeUser = saveUser(taskAssigneeEmail, userPassword);
        User projectReadOnlyMemberUser = saveUser(projectReadOnlyMemberEmail, userPassword);
        saveUser(nonMemberEmail, userPassword);

        Project privateProject = saveProject(projectOwnerUser, privateProjectName, privateProjectUiId, true);
        Project publicProject = saveProject(projectOwnerUser, publicProjectName, publicProjectUiId, false);

        for (Project project : new Project[] { privateProject, publicProject }) {
            saveProjectMember(projectManagerUser, project, true, true);
            saveProjectMember(projectMemberUser, project, false, true);
            saveProjectMember(taskAssigneeUser, project, false, false);
            saveProjectMember(projectReadOnlyMemberUser, project, false, false);
        }
    }

    @After
    public void afterTest() {
        testRepo.clearTable(ProjectMember.class);
        testRepo.clearTable(Project.class);
        testRepo.clearTable(ProjectTaskNumberSequence.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_delete() throws Exception {
        User projectOwner = userRepo.getByEmail(projectOwnerEmail);
        User otherUser = userRepo.getByEmail(projectManagerEmail);
        Project project = projectRepo.getByUiId(privateProjectUiId);
        ProjectMember otherProjectMember = projectMemberRepo.getNotFiredByUserAndProject(otherUser, project);
        Long otherProjectMemberId = otherProjectMember.getId();
        long numberOfProjectMembers = testRepo.countEntities(ProjectMember.class);

        projectMemberService.delete(projectOwner, otherProjectMemberId);

        ProjectMember deletedProjectMember = testRepo.getEntityOrNull(ProjectMember.class, otherProjectMemberId);
        assertNull("project member must be deleted", deletedProjectMember);
        long numberOfProjectMembersAfterTest = testRepo.countEntities(ProjectMember.class);
        assertEquals("exactly 1 project member should be deleted", numberOfProjectMembers - 1, numberOfProjectMembersAfterTest);
    }

    @Test
    public void test_leave() throws Exception {
        User projectReadOnlyMemberUser = userRepo.getByEmail(projectReadOnlyMemberEmail);
        Project project = projectRepo.getByUiId(privateProjectUiId);
        ProjectMember projectReadOnlyMember = projectMemberRepo.getNotFiredByUserAndProject(projectReadOnlyMemberUser, project);
        Long projectReadOnlyMemberId = projectReadOnlyMember.getId();
        long numberOfProjectMembers = testRepo.countEntities(ProjectMember.class);

        projectMemberService.leave(projectReadOnlyMemberUser, project.getId());

        ProjectMember deletedProjectMember = testRepo.getEntityOrNull(ProjectMember.class, projectReadOnlyMemberId);
        assertNull("project member must be deleted", deletedProjectMember);
        long numberOfProjectMembersAfterTest = testRepo.countEntities(ProjectMember.class);
        assertEquals("exactly 1 project member should be deleted", numberOfProjectMembers - 1, numberOfProjectMembersAfterTest);
    }
}
