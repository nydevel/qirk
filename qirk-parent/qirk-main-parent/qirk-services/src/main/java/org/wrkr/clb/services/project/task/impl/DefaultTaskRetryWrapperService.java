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
