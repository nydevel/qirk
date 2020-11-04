package org.wrkr.clb.web.controller.project.task;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.dto.project.task.TaskIdDTO;
import org.wrkr.clb.services.dto.user.UserIdsDTO;
import org.wrkr.clb.services.project.task.TaskSubscriberService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "task-subscriber", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskSubscriberController extends BaseExceptionHandlerController {

    @Autowired
    private TaskSubscriberService subscriberService;

    @PostMapping(value = "/")
    public JsonContainer<Void, Void> create(HttpSession session, @RequestBody TaskIdDTO idDTO) {
        long startTime = System.currentTimeMillis();
        subscriberService.create(getSessionUser(session), idDTO.taskId);
        logProcessingTimeFromStartTime(startTime, "create", idDTO.taskId);
        return new JsonContainer<Void, Void>();
    }

    @GetMapping(value = "list")
    public JsonContainer<UserIdsDTO, Void> list(HttpSession session, @RequestParam(name = "task_id") Long taskId) {
        long startTime = System.currentTimeMillis();
        UserIdsDTO usersDTO = subscriberService.list(getSessionUser(session), taskId);
        logProcessingTimeFromStartTime(startTime, "list", taskId);
        return new JsonContainer<UserIdsDTO, Void>(usersDTO);
    }

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session, @RequestBody TaskIdDTO idDTO) {
        long startTime = System.currentTimeMillis();
        subscriberService.delete(getSessionUser(session), idDTO.taskId);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.taskId);
        return new JsonContainer<Void, Void>();
    }
}
