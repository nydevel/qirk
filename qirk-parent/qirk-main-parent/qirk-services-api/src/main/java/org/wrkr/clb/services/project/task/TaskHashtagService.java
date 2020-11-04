package org.wrkr.clb.services.project.task;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.task.TaskHashtagDTO;

@Validated
public interface TaskHashtagService {

    public List<TaskHashtagDTO> searchByProjectId(User currentUser,
            @NotNull(message = "prefix must not be null") String prefix,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<TaskHashtagDTO> searchByProjectUiId(User currentUser,
            @NotNull(message = "prefix must not be null") String prefix,
            @NotNull(message = "projectUiId must not be null") String projectUiId);
}
