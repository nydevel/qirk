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
package org.wrkr.clb.elasticsearch.datasync.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.JDBCGrantedPermissionsProjectInviteRepo;
import org.wrkr.clb.repo.project.JDBCProjectInviteRepo;
import org.wrkr.clb.repo.project.task.TaskRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;

public class DatabaseService {

    @Autowired
    private JDBCUserRepo userRepo;

    @Autowired
    private JDBCGrantedPermissionsProjectInviteRepo grantedPermissionsProjectInviteRepo;

    @Autowired
    private JDBCProjectInviteRepo projectInviteRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<User> getAllUsers() {
        return userRepo.listAndFetchProjectMembership();
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Long> getInvitedProjectIdsByUser(User user, InviteStatus status) {
        List<Long> invitedProjectIds = grantedPermissionsProjectInviteRepo.listProjectIdsByUserIdAndStatusId(user.getId(),
                status.getId());
        invitedProjectIds.addAll(projectInviteRepo.listProjectIdsByUserIdAndStatusId(user.getId(), status.getId()));
        return invitedProjectIds;
    }

    @Transactional(value = "jpaTransactionManager", readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepo.listAndFetchTypeAndPriorityAndStatusAndHashtags();
    }
}
