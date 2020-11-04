package org.wrkr.clb.web.controller.project.task;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.services.project.task.TaskService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "task-type", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskTypeController extends BaseExceptionHandlerController {

    @Autowired
    private TaskService taskService;

    @GetMapping(value = "list")
    public JsonContainer<TaskType, Void> list(@SuppressWarnings("unused") HttpSession session) {
        long startTime = System.currentTimeMillis();
        List<TaskType> taskTypeList = taskService.listTaskTypes();
        logProcessingTimeFromStartTime(startTime, "list");
        return new JsonContainer<TaskType, Void>(taskTypeList);
    }
}
