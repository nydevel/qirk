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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.jdbc.transaction.Executor;
import org.wrkr.clb.common.jdbc.transaction.RetryOnCannotAcquireLock;
import org.wrkr.clb.common.jdbc.transaction.RetryOnDuplicateKey;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.task.TaskDTO;
import org.wrkr.clb.services.dto.project.task.TaskReadDTO;
import org.wrkr.clb.services.project.task.TaskRetryWrapperService;
import org.wrkr.clb.services.project.task.TaskService;


@Service
public class DefaultTaskRetryWrapperService implements TaskRetryWrapperService {

    @Autowired
    private TaskService taskService;

    @Override
    public TaskReadDTO create(User currentUser, TaskDTO taskDTO) throws Exception {
        return RetryOnDuplicateKey.<TaskReadDTO>exec(new Executor() {
            @SuppressWarnings("unchecked")
            @Override
            public TaskReadDTO exec(int retryNumber) throws Exception {
                return taskService.create(currentUser, taskDTO, retryNumber);
            }
        });
    }

    @Override
    public TaskReadDTO update(User currentUser, TaskDTO taskDTO) throws Exception {
        return RetryOnCannotAcquireLock.<TaskReadDTO>exec(new Executor() {
            @SuppressWarnings({ "unchecked", "unused" })
            @Override
            public TaskReadDTO exec(int retryNumber) throws Exception {
                return taskService.update(currentUser, taskDTO);
            }
        });
    }
}
