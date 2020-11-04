package org.wrkr.clb.web.controller.project.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.repo.context.TaskSearchContext;
import org.wrkr.clb.repo.sort.SortingOption;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.PaginatedListDTO;
import org.wrkr.clb.services.dto.meta.PaginationMetaDTO;
import org.wrkr.clb.services.dto.project.task.LinkedTaskDTO;
import org.wrkr.clb.services.dto.project.task.SearchedTaskDTO;
import org.wrkr.clb.services.dto.project.task.TaskCardIdDTO;
import org.wrkr.clb.services.dto.project.task.TaskDTO;
import org.wrkr.clb.services.dto.project.task.TaskLinkDTO;
import org.wrkr.clb.services.dto.project.task.TaskReadDTO;
import org.wrkr.clb.services.project.task.TaskRetryWrapperService;
import org.wrkr.clb.services.project.task.TaskService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "task", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskController extends BaseExceptionHandlerController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRetryWrapperService taskRWService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<TaskReadDTO, Void> create(HttpSession session,
            @RequestBody TaskDTO taskDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        TaskReadDTO taskReadDTO = taskRWService.create(getSessionUser(session), taskDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<TaskReadDTO, Void>(taskReadDTO);
    }

    @PutMapping(value = "/")
    public JsonContainer<TaskReadDTO, Void> update(HttpSession session,
            @RequestBody TaskDTO taskDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        TaskReadDTO taskReadDTO = taskRWService.update(getSessionUser(session), taskDTO);
        logProcessingTimeFromStartTime(startTime, "update", taskDTO.id);
        return new JsonContainer<TaskReadDTO, Void>(taskReadDTO);
    }

    @PutMapping(value = "card")
    public JsonContainer<TaskCardIdDTO, Void> updateCard(HttpSession session,
            @RequestBody TaskCardIdDTO taskDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        TaskCardIdDTO taskReadDTO = taskService.updateCard(getSessionUser(session), taskDTO);
        logProcessingTimeFromStartTime(startTime, "updateCard", taskDTO.id);
        return new JsonContainer<TaskCardIdDTO, Void>(taskReadDTO);
    }

    @PostMapping(value = "link")
    public JsonContainer<Void, Void> addLink(HttpSession session,
            @RequestBody TaskLinkDTO taskLinkDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        taskService.addLink(getSessionUser(session), taskLinkDTO);
        logProcessingTimeFromStartTime(startTime, "addLink");
        return new JsonContainer<Void, Void>();
    }

    @DeleteMapping(value = "link")
    public JsonContainer<Void, Void> removeLink(HttpSession session,
            @RequestBody TaskLinkDTO taskLinkDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        taskService.removeLink(getSessionUser(session), taskLinkDTO);
        logProcessingTimeFromStartTime(startTime, "removeLink");
        return new JsonContainer<Void, Void>();
    }

    @GetMapping(value = "/")
    public JsonContainer<TaskReadDTO, Void> read(HttpSession session,
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId,
            @RequestParam(name = "number", required = false) Long number) throws Exception {
        long startTime = System.currentTimeMillis();
        TaskReadDTO taskDTO = null;
        if (id != null) {
            taskDTO = taskService.get(getSessionUser(session), id);
        } else if (number != null) {
            taskDTO = taskService.getByProjectAndNumber(getSessionUser(session),
                    new IdOrUiIdDTO(projectId, projectUiId), number);
        } else {
            throw new BadRequestException(
                    "Neither parameter 'id' nor parameter 'number' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "read", id, projectId, projectUiId, number);
        return new JsonContainer<TaskReadDTO, Void>(taskDTO);
    }

    @GetMapping(value = "list-link-options")
    public JsonContainer<LinkedTaskDTO, Void> listLinkOptions(HttpSession session,
            @RequestParam(name = "task_id", required = false) Long taskId,
            @RequestParam(name = "project_id", required = false) Long projectId) throws BadRequestException {
        long startTime = System.currentTimeMillis();
        List<LinkedTaskDTO> taskDTOList = new ArrayList<LinkedTaskDTO>();
        if (taskId != null) {
            taskDTOList = taskService.listLinkOptionsByTaskId(getSessionUser(session), taskId);
        } else if (projectId != null) {
            taskDTOList = taskService.listLinkOptionsByProjectId(getSessionUser(session), projectId);
        } else {
            throw new BadRequestException("Neither parameter 'task_id' nor parameter 'project_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "listLinkOptions", taskId, projectId);
        return new JsonContainer<LinkedTaskDTO, Void>(taskDTOList);
    }

    @GetMapping(value = "list-cardless")
    public JsonContainer<TaskReadDTO, Void> listCardless(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) {
        long startTime = System.currentTimeMillis();
        List<TaskReadDTO> taskDTOList = taskService.listCardless(getSessionUser(session),
                new IdOrUiIdDTO(projectId, projectUiId));
        logProcessingTimeFromStartTime(startTime, "listCardless", projectId, projectUiId);
        return new JsonContainer<TaskReadDTO, Void>(taskDTOList);
    }

    private <T extends Enum<T>> T getEnum(Class<T> enumClass, String parameterName, String value) throws BadRequestException {
        try {
            return Enum.valueOf(enumClass, value.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Parameter '" + parameterName + "' is invalid");
        }
    }

    /*@formatter:off
    @GetMapping(value = "search")
    public JsonContainer<TaskReadDTO, Void> search(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId,
            @RequestParam(name = "reported_by_me", required = false, defaultValue = "false") boolean reportedByMe,
            @RequestParam(name = "reporter_id", required = false) Long reporterId,
            @RequestParam(name = "assigned_to_me", required = false, defaultValue = "false") boolean assignedToMe,
            @RequestParam(name = "assignee_id", required = false) Long assigneeId,
            @RequestParam(name = "alive", required = false, defaultValue = "false") boolean alive,
            @RequestParam(name = "type", required = false, defaultValue = "") List<String> types,
            @RequestParam(name = "priority", required = false, defaultValue = "") List<String> priorities,
            @RequestParam(name = "status", required = false, defaultValue = "") List<String> statuses,
            @RequestParam(name = "sort_by", required = false, defaultValue = "UPDATED_AT") String sortByString,
            @RequestParam(name = "ordering", required = false, defaultValue = "DESC") String orderingString,
            @RequestParam(name = "hashtag", required = false, defaultValue = "") String hashtag)
            throws Exception {
        long startTime = System.currentTimeMillis();

        SortingOption.ForTask sortBy = getEnum(SortingOption.ForTask.class, "sort_by", sortByString);
        SortingOption.Order ordering = getEnum(SortingOption.Order.class, "ordering", orderingString);
        TaskSearchContext searchContext = new TaskSearchContext(new IdOrUiIdDTO(projectId, projectUiId), reporterId, assigneeId,
                types, priorities, statuses, "", hashtag, sortBy, ordering, null);

        User currentUser = getSessionUser(session);
        if (currentUser != null) {
            if (reportedByMe) {
                searchContext.reporterId = TaskSearchContext.REPORTED_BY_ME_ID;
            }
            if (assignedToMe) {
                searchContext.assigneeId = TaskSearchContext.ASSIGNED_TO_ME_ID;
            }
        }

        List<TaskReadDTO> taskDTOList = taskService.search(currentUser, searchContext);
        logProcessingTimeFromStartTime(startTime, "search", projectId, projectUiId,
                reportedByMe, reporterId, assignedToMe, assigneeId,
                alive, types, priorities, statuses, sortByString, orderingString);
        return new JsonContainer<TaskReadDTO, Void>(taskDTOList);
    }
    @formatter:on*/

    @GetMapping(value = "text-search")
    public JsonContainer<SearchedTaskDTO, PaginationMetaDTO> textSearch(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId,
            @RequestParam(name = "reporter_id", required = false) Long reporterId,
            @RequestParam(name = "assignee_id", required = false) Long assigneeId,
            @RequestParam(name = "type", required = false, defaultValue = "") List<String> types,
            @RequestParam(name = "priority", required = false, defaultValue = "") List<String> priorities,
            @RequestParam(name = "status", required = false, defaultValue = "") List<String> statuses,
            @RequestParam(name = "text", required = false, defaultValue = "") String text,
            @RequestParam(name = "hashtag", required = false, defaultValue = "") String hashtag,
            @RequestParam(name = "sort_by", required = false, defaultValue = "UPDATED_AT") String sortByString,
            @RequestParam(name = "ordering", required = false, defaultValue = "DESC") String sortOrderString,
            @RequestParam(name = "search_after", required = false) Long searchAfter)
            throws Exception {
        long startTime = System.currentTimeMillis();

        SortingOption.ForTask sortBy = getEnum(SortingOption.ForTask.class, "sort_by", sortByString);
        SortingOption.Order sortOrder = getEnum(SortingOption.Order.class, "ordering", sortOrderString);
        TaskSearchContext searchContext = new TaskSearchContext(new IdOrUiIdDTO(projectId, projectUiId), reporterId, assigneeId,
                types, priorities, statuses, text, hashtag, sortBy, sortOrder, searchAfter);

        PaginatedListDTO<SearchedTaskDTO> taskDTOList = taskService.textSearch(getSessionUser(session), searchContext);
        logProcessingTimeFromStartTime(startTime, "textSearch", projectId, projectUiId,
                reporterId, assigneeId,
                types, priorities, statuses, sortByString, sortOrderString, searchAfter);
        return JsonContainer.fromPaginatedList(taskDTOList);
    }

    @GetMapping(value = "chat-token")
    public JsonContainer<ChatPermissionsDTO, Void> getChatToken(HttpSession session,
            @RequestParam(name = "id") long id) throws Exception {
        long startTime = System.currentTimeMillis();
        ChatPermissionsDTO tokenDTO = taskService.getChatToken(getSessionUser(session), id);
        logProcessingTimeFromStartTime(startTime, "getChatToken", id);
        return new JsonContainer<ChatPermissionsDTO, Void>(tokenDTO);
    }
}
