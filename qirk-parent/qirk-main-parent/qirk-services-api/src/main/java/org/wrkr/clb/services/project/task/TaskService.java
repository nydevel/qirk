package org.wrkr.clb.services.project.task;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.context.TaskSearchContext;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.PaginatedListDTO;
import org.wrkr.clb.services.dto.project.task.LinkedTaskDTO;
import org.wrkr.clb.services.dto.project.task.SearchedTaskDTO;
import org.wrkr.clb.services.dto.project.task.TaskCardIdDTO;
import org.wrkr.clb.services.dto.project.task.TaskDTO;
import org.wrkr.clb.services.dto.project.task.TaskLinkDTO;
import org.wrkr.clb.services.dto.project.task.TaskReadDTO;

@Validated
public interface TaskService {

    @Validated(OnCreate.class)
    public TaskReadDTO create(User currentUser, @Valid TaskDTO taskDTO, int retryNumber) throws Exception;

    @Validated(OnUpdate.class)
    public TaskReadDTO update(User currentUser,
            @Valid TaskDTO taskDTO) throws Exception;

    public TaskCardIdDTO updateCard(User currentUser, @Valid TaskCardIdDTO taskDTO) throws Exception;

    public void addLink(User currentUser, @Valid TaskLinkDTO taskLinkDTO) throws Exception;

    public void removeLink(User currentUser, @Valid TaskLinkDTO taskLinkDTO) throws Exception;

    public TaskReadDTO get(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public TaskReadDTO getByProjectAndNumber(User currentUser, @Valid IdOrUiIdDTO projectDTO,
            @NotNull(message = "number must not be null") Long number)
            throws Exception;

    public List<LinkedTaskDTO> listLinkOptionsByTaskId(User currentUser,
            @NotNull(message = "taskId must not be null") Long taskId);

    public List<LinkedTaskDTO> listLinkOptionsByProjectId(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<TaskReadDTO> listCardless(User currentUser, @Valid IdOrUiIdDTO idOrUiIdDTO);

    @Deprecated
    public List<TaskReadDTO> search(User currentUser, @Valid TaskSearchContext searchContext) throws Exception;

    public PaginatedListDTO<SearchedTaskDTO> textSearch(User currentUser, @Valid TaskSearchContext searchContext)
            throws Exception;

    public ChatPermissionsDTO getChatToken(User currentUser, long taskId) throws Exception;

    public List<TaskType> listTaskTypes();

    public List<TaskPriority> listTaskPriorities();

    public List<TaskStatus> listTaskStatuses();
}
