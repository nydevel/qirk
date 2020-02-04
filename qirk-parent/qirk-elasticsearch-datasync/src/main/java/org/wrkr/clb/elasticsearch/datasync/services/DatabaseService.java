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
package org.wrkr.clb.elasticsearch.datasync.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.InviteStatus;
import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.InviteStatusRepo;
import org.wrkr.clb.repo.TagRepo;
import org.wrkr.clb.repo.organization.OrganizationRepo;
import org.wrkr.clb.repo.project.JDBCGrantedPermissionsProjectInviteRepo;
import org.wrkr.clb.repo.project.JDBCProjectInviteRepo;
import org.wrkr.clb.repo.project.JDBCProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.project.task.TaskRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;

public class DatabaseService {

    @Autowired
    private JDBCUserRepo userRepo;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private JDBCProjectMemberRepo projectMemberRepo;

    @Autowired
    private JDBCProjectInviteRepo projectInviteRepo;

    @Autowired
    private JDBCGrantedPermissionsProjectInviteRepo grantedPermsProjectInviteRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private InviteStatusRepo inviteStatusRepo;

    public InviteStatus getInviteStatus(InviteStatus.Status status) {
        return inviteStatusRepo.getByNameCode(status);
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<User> getAllUsers() {
        return userRepo.listAndFetchOrganizationMembership();
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Tag> getTagsByUser(User user) {
        return tagRepo.listByUserId(user.getId());
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Long> getProjectIdsByMemberUser(User user) {
        return projectMemberRepo.listProjectIdsByNotFiredUserId(user.getId());
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Long> getInvitedProjectIdsByUser(User user, InviteStatus status) {
        Set<Long> invitedProjectIds = new HashSet<Long>(projectInviteRepo.listProjectIdsByUserIdAndStatusId(
                user.getId(), status.getId()));
        invitedProjectIds.addAll(grantedPermsProjectInviteRepo.listProjectIdsByUserIdAndStatusId(user.getId(), status.getId()));
        return new ArrayList<Long>(invitedProjectIds);
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Organization> getAllOrganizations() {
        return organizationRepo.list();
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepo.listAndFetchTags();
    }

    @Transactional(value = "jpaTransactionManager", readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepo.listAndFetchTypeAndPriorityAndStatusAndHashtags();
    }
}
