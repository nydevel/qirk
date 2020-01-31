/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.project.task.TaskHashtagDTO;
import org.wrkr.clb.services.project.task.TaskHashtagService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "task-hashtag", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskHashtagController extends BaseExceptionHandlerController {

    @Autowired
    private TaskHashtagService hashtagService;

    @GetMapping(value = "search")
    public JsonContainer<TaskHashtagDTO, Void> list(HttpSession session,
            @RequestParam(name = "prefix", required = false, defaultValue = "") String prefix,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId,
            @RequestParam(name = "include_used", required = false, defaultValue = "false") Boolean includeUsed)
            throws BadRequestException {
        long startTime = System.currentTimeMillis();
        List<TaskHashtagDTO> hashtagDTOlist = new ArrayList<TaskHashtagDTO>();
        if (projectId != null) {
            hashtagDTOlist = hashtagService.searchByProjectId(getSessionUser(session), prefix, projectId, includeUsed);
        } else if (projectUiId != null) {
            hashtagDTOlist = hashtagService.searchByProjectUiId(getSessionUser(session), prefix, projectUiId.strip(), includeUsed);
        } else {
            throw new BadRequestException("Neither parameter 'project_id' nor parameter 'project_ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "search", prefix, projectId, projectUiId, includeUsed);
        return new JsonContainer<TaskHashtagDTO, Void>(hashtagDTOlist);
    }

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session, @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        hashtagService.delete(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
