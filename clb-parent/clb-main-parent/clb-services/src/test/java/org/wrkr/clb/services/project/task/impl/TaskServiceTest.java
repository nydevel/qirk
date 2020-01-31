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
package org.wrkr.clb.services.project.task.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.project.task.TaskService;


@SuppressWarnings("unused")
public class TaskServiceTest extends BaseServiceTest {

    private static final String orgOwnerEmail = "org_owner@test.com";

    private static User user1;
    private static final String user1email = "user1@test.com";

    private static User user2;
    private static final String user2email = "user2@test.com";

    private static Organization organization1;
    private static final String organization1name = "Organization 1";
    private static final String organization1uiId = "organization_1";

    private static OrganizationMember user1organizationMember;
    private static OrganizationMember user2organizationMember;

    private static Project project1;
    private static final String project1name = "Project 1";
    private static final String project1uiId = "project_1";

    private static Project project2;
    private static final String project2name = "Project 2";
    private static final String project2uiId = "project_2";

    private static List<OrganizationMember> reporters;
    private static List<OrganizationMember> assignees;
    private static List<Project> projects;
    private static List<TaskType> types;
    private static List<TaskPriority> priorities;
    private static List<TaskStatus> statuses;

    @Autowired
    private TaskService taskService;

    @Before
    public void beforeTest() throws Exception {
        User orgOwner = saveUser(orgOwnerEmail);
        user1 = saveUser(user1email);
        user2 = saveUser(user2email);

        organization1 = saveOrganization(orgOwner, organization1name, organization1uiId, false);

        user1organizationMember = saveOrganizationMember(user1, organization1, false);
        user2organizationMember = saveOrganizationMember(user2, organization1, false);

        project1 = saveProject(organization1, project1name, project1uiId, false);
        project2 = saveProject(organization1, project2name, project2uiId, false);

        reporters = new ArrayList<OrganizationMember>(Arrays.asList(user1organizationMember, user2organizationMember));
        assignees = new ArrayList<OrganizationMember>(Arrays.asList(user1organizationMember, user2organizationMember, null));
        projects = new ArrayList<Project>(Arrays.asList(project1, project2));

        types = new ArrayList<TaskType>(testRepo.listEntities(TaskType.class));
        priorities = new ArrayList<TaskPriority>(testRepo.listEntities(TaskPriority.class));
        statuses = new ArrayList<TaskStatus>(testRepo.listEntities(TaskStatus.class));

        for (Project project : projects) {
            int[] sizes = { reporters.size(), assignees.size(), types.size(), priorities.size(), statuses.size() };
            int numberOfTasks = NumberUtils.max(sizes);
            List<Task> tasks = new ArrayList<Task>();
            for (int iter = 0; iter < numberOfTasks; iter++) {
                tasks.add(createTask(
                        reporters.get(iter % reporters.size()),
                        assignees.get(iter % assignees.size()),
                        types.get(iter % types.size()),
                        priorities.get(iter % priorities.size()),
                        statuses.get(iter % statuses.size()),
                        null));
            }
            saveTasks(project, tasks);
        }
    }

    @After
    public void afterTest() {
        testRepo.clearTable(Task.class);
        testRepo.clearTable(Project.class);
        testRepo.clearTable(ProjectTaskNumberSequence.class);
        testRepo.clearTable(OrganizationMember.class);
        testRepo.clearTable(Organization.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_() {

    }
}
