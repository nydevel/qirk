package org.wrkr.clb.web.controller.project.task;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.services.project.task.TaskService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "task-status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskStatusController extends BaseExceptionHandlerController {

    @Autowired
    private TaskService taskService;

    @GetMapping(value = "list")
    public JsonContainer<TaskStatus, Void> list(@SuppressWarnings("unused") HttpSession session) {
        long startTime = System.currentTimeMillis();
        List<TaskStatus> taskStatusList = taskService.listTaskStatuses();
        logProcessingTimeFromStartTime(startTime, "list");
        return new JsonContainer<TaskStatus, Void>(taskStatusList);
    }
}
