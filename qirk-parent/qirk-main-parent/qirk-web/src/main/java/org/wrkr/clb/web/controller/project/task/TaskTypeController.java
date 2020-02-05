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
