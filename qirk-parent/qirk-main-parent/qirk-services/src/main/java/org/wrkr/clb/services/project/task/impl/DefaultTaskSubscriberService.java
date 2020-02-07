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
package org.wrkr.clb.services.project.task.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.task.TaskSubscriberRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.services.dto.user.UserIdsDTO;
import org.wrkr.clb.services.project.task.TaskSubscriberService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.security.SecurityService;

@Service
public class DefaultTaskSubscriberService implements TaskSubscriberService {

    @Autowired
    private TaskSubscriberRepo subscriberRepo;

    @Autowired
    private JDBCUserRepo userRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private SecurityService authnSecurityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public void create(Long userId, Long taskId) {
        subscriberRepo.save(userId, taskId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void create(User currentUser, Long taskId) {
        // security
        securityService.authzCanSubscribeToTask(currentUser, taskId);
        // security
        Long userId = currentUser.getId();
        if (!subscriberRepo.exists(userId, taskId)) {
            create(userId, taskId);
        }
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Long> list(Long taskId) {
        return subscriberRepo.listUserIdsByTaskId(taskId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public UserIdsDTO list(User currentUser, Long taskId) {
        // security
        securityService.authzCanReadTask(currentUser, taskId);
        // security
        return new UserIdsDTO(list(taskId));
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<User> listWithEmail(Long taskId, NotificationSettings.Setting notifSetting) {
        return userRepo.listBySubscribedTaskIdAndFetchNotificationSetting(taskId, notifSetting);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public void delete(Long userId, Long taskId) {
        subscriberRepo.delete(taskId, userId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void delete(User currentUser, Long taskId) {
        // security
        authnSecurityService.isAuthenticated(currentUser);
        // security
        delete(currentUser.getId(), taskId);
    }
}
