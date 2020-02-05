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
