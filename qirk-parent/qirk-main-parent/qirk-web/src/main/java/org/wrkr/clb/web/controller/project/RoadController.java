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
package org.wrkr.clb.web.controller.project;

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
import org.wrkr.clb.services.dto.MoveDTO;
import org.wrkr.clb.services.dto.project.RoadDTO;
import org.wrkr.clb.services.dto.project.RoadReadDTO;
import org.wrkr.clb.services.project.RoadService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "road", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RoadController extends BaseExceptionHandlerController {

    @Autowired
    private RoadService roadService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<RoadReadDTO, Void> create(HttpSession session,
            @RequestBody RoadDTO roadDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        RoadReadDTO roadReadDTO = roadService.create(getSessionUser(session), roadDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<RoadReadDTO, Void>(roadReadDTO);
    }

    @PutMapping(value = "/")
    public JsonContainer<RoadReadDTO, Void> update(HttpSession session,
            @RequestBody RoadDTO roadDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        RoadReadDTO roadReadDTO = roadService.update(getSessionUser(session), roadDTO);
        logProcessingTimeFromStartTime(startTime, "update", roadDTO.id);
        return new JsonContainer<RoadReadDTO, Void>(roadReadDTO);
    }

    @PutMapping(value = "move")
    public JsonContainer<Void, Void> move(HttpSession session,
            @RequestBody MoveDTO moveDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        roadService.move(getSessionUser(session), moveDTO);
        logProcessingTimeFromStartTime(startTime, "move", moveDTO.id);
        return new JsonContainer<Void, Void>();
    }

    @GetMapping(value = "list")
    public JsonContainer<RoadReadDTO, Void> list(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) {
        long startTime = System.currentTimeMillis();
        List<RoadReadDTO> roadDTOList = new ArrayList<RoadReadDTO>();
        if (projectId != null) {
            roadDTOList = roadService.list(getSessionUser(session), projectId);
        } else if (projectUiId != null) {
            roadDTOList = roadService.list(getSessionUser(session), projectUiId);
        }
        logProcessingTimeFromStartTime(startTime, "list", projectId, projectUiId);
        return new JsonContainer<RoadReadDTO, Void>(roadDTOList);
    }

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session,
            @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        roadService.delete(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
