package org.wrkr.clb.services.project.task;

import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.task.TaskDTO;
import org.wrkr.clb.services.dto.project.task.TaskReadDTO;

public interface TaskRetryWrapperService {

    public TaskReadDTO create(User currentUser, TaskDTO taskDTO) throws Exception;

    public TaskReadDTO update(User currentUser, TaskDTO taskDTO) throws Exception;
}
