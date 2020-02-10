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
package org.wrkr.clb.web.controller.project.roadmap;

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
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.VersionedEntityDTO;
import org.wrkr.clb.services.dto.project.MoveToRoadDTO;
import org.wrkr.clb.services.dto.project.roadmap.TaskCardDTO;
import org.wrkr.clb.services.dto.project.roadmap.TaskCardReadDTO;
import org.wrkr.clb.services.project.roadmap.TaskCardService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "task-card", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskCardController extends BaseExceptionHandlerController {

    @Autowired
    private TaskCardService cardService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<TaskCardReadDTO, Void> create(HttpSession session,
            @RequestBody TaskCardDTO cardDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        TaskCardReadDTO cardReadDTO = cardService.create(getSessionUser(session), cardDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<TaskCardReadDTO, Void>(cardReadDTO);
    }

    @PutMapping(value = "/")
    public JsonContainer<TaskCardReadDTO, Void> update(HttpSession session,
            @RequestBody TaskCardDTO cardDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        TaskCardReadDTO cardReadDTO = cardService.update(getSessionUser(session), cardDTO);
        logProcessingTimeFromStartTime(startTime, "update", cardDTO.id);
        return new JsonContainer<TaskCardReadDTO, Void>(cardReadDTO);
    }

    @PutMapping(value = "move")
    public JsonContainer<Void, Void> move(HttpSession session,
            @RequestBody MoveToRoadDTO moveDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        cardService.move(getSessionUser(session), moveDTO);
        logProcessingTimeFromStartTime(startTime, "move", moveDTO.id, moveDTO.roadId);
        return new JsonContainer<Void, Void>();
    }

    @GetMapping(value = "list")
    public JsonContainer<TaskCardReadDTO, Void> list(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) throws Exception {
        long startTime = System.currentTimeMillis();
        List<TaskCardReadDTO> cardDTOList = new ArrayList<TaskCardReadDTO>();
        if (projectId != null) {
            cardDTOList = cardService.list(getSessionUser(session), projectId);
        } else if (projectUiId != null) {
            cardDTOList = cardService.list(getSessionUser(session), projectUiId);
        }
        logProcessingTimeFromStartTime(startTime, "list", projectId, projectUiId);
        return new JsonContainer<TaskCardReadDTO, Void>(cardDTOList);
    }

    @PutMapping(value = "archive")
    public JsonContainer<Void, Void> archive(HttpSession session,
            @RequestBody VersionedEntityDTO dto) throws Exception {
        long startTime = System.currentTimeMillis();
        cardService.archive(getSessionUser(session), dto);
        logProcessingTimeFromStartTime(startTime, "archive", dto.id);
        return new JsonContainer<Void, Void>();
    }

    @GetMapping(value = "archive")
    public JsonContainer<TaskCardReadDTO, Void> listArchive(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) throws Exception {
        long startTime = System.currentTimeMillis();
        List<TaskCardReadDTO> cardDTOList = cardService.listArchive(getSessionUser(session), projectId);
        logProcessingTimeFromStartTime(startTime, "listArchive", projectId, projectUiId);
        return new JsonContainer<TaskCardReadDTO, Void>(cardDTOList);
    }

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session,
            @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        cardService.delete(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
