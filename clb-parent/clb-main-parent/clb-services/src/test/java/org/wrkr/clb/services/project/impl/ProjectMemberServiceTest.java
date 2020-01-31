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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.organization.OrganizationMemberRepo;
import org.wrkr.clb.repo.organization.OrganizationRepo;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.impl.TransactionalService;
import org.wrkr.clb.services.project.ProjectMemberService;

public class ProjectMemberServiceTest extends BaseServiceTest {

    private static final String userPassword = "password";
    private static final String orgOwnerEmail = "org_owner@test.com";
    private static final String orgManagerEmail = "org_manager@test.com";
    private static final String projectManagerEmail = "project_manager@test.com";
    private static final String projectMemberEmail = "project_member@test.com";
    private static final String taskAssigneeEmail = "task_assignee@test.com";
    private static final String projectReadOnlyMemberEmail = "project_read_only_member@test.com";
    private static final String nonMemberEmail = "non_member@test.com";

    private static final String publicOrganizationName = "Public Organization";
    private static final String publicOrganizationUiId = "public_organization";

    private static final String privateProjectName = "Private Project";
    private static final String privateProjectUiId = "private_project";

    private static final String publicProjectName = "Public Project";
    private static final String publicProjectUiId = "public_project";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationMemberRepo organizationMemberRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private TransactionalService transactionalService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void beforeTest() throws Exception {
        User orgOwnerUser = saveUser(orgOwnerEmail, userPassword);
        User orgManagerUser = saveUser(orgManagerEmail, userPassword);
        User projectManagerUser = saveUser(projectManagerEmail, userPassword);
        User projectMemberUser = saveUser(projectMemberEmail, userPassword);
        User taskAssigneeUser = saveUser(taskAssigneeEmail, userPassword);
        User projectReadOnlyMemberUser = saveUser(projectReadOnlyMemberEmail, userPassword);
        saveUser(nonMemberEmail, userPassword);

        Organization publicOrganization = saveOrganization(orgOwnerUser, publicOrganizationName, publicOrganizationUiId, false);

        saveOrganizationMember(orgManagerUser, publicOrganization, true);
        OrganizationMember projectManager = saveOrganizationMember(projectManagerUser, publicOrganization, false);
        OrganizationMember projectMember = saveOrganizationMember(projectMemberUser, publicOrganization, false);
        OrganizationMember taskAssignee = saveOrganizationMember(taskAssigneeUser, publicOrganization, false);
        OrganizationMember projectReadOnlyMember = saveOrganizationMember(projectReadOnlyMemberUser, publicOrganization, false);

        Project privateProject = saveProject(publicOrganization, privateProjectName, privateProjectUiId, true);
        Project publicProject = saveProject(publicOrganization, publicProjectName, publicProjectUiId, false);

        for (Project project : new Project[] { privateProject, publicProject }) {
            saveProjectMember(projectManager, project, true, true);
            saveProjectMember(projectMember, project, false, true);
            saveProjectMember(taskAssignee, project, false, false);
            saveProjectMember(projectReadOnlyMember, project, false, false);
        }
    }

    @After
    public void afterTest() {
        testRepo.clearTable(ProjectMember.class);
        testRepo.clearTable(Project.class);
        testRepo.clearTable(ProjectTaskNumberSequence.class);
        testRepo.clearTable(OrganizationMember.class);
        testRepo.clearTable(Organization.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_delete() throws Exception {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        User otherUser = userRepo.getByEmail(projectManagerEmail);
        Project project = projectRepo.getByUiId(privateProjectUiId);
        ProjectMember otherProjectMember = projectMemberRepo.getNotFiredByUserAndProject(otherUser, project);
        Long otherProjectMemberId = otherProjectMember.getId();
        long numberOfProjectMembers = testRepo.countEntities(ProjectMember.class);

        projectMemberService.delete(orgManager, otherProjectMemberId);

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

    @Test
    public void test_deleteBatchByOrganizationMemberId() throws Exception {
        User user = userRepo.getByEmail(projectManagerEmail);
        Organization organization = organizationRepo.getByUiId(publicOrganizationUiId);
        OrganizationMember orgMemberToDelete = organizationMemberRepo.getNotFiredByUserAndOrganization(user, organization);
        List<ProjectMember> projectMembersToDelete = projectMemberRepo.listNotFiredByOrganizationMember(orgMemberToDelete);
        long numberOfProjectMembers = testRepo.countEntities(ProjectMember.class);
        long numberOfProjectMembersToDelete = projectMembersToDelete.size();

        transactionalService.projectMemberService_deleteBatchByOrganizationMember(orgMemberToDelete);

        for (ProjectMember projectMember : projectMembersToDelete) {
            ProjectMember deletedProjectMember = testRepo.getEntityOrNull(ProjectMember.class, projectMember.getId());
            assertNull("project member must be deleted", deletedProjectMember);
        }
        long numberOfProjectMembersAfterTest = testRepo.countEntities(ProjectMember.class);
        assertEquals("exactly " + numberOfProjectMembersToDelete + " project members should be deleted",
                numberOfProjectMembers - numberOfProjectMembersToDelete, numberOfProjectMembersAfterTest);
    }
}
