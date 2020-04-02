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
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberListDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberReadDTO;
import org.wrkr.clb.services.project.ProjectMemberService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "project-member", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ProjectMemberController extends BaseExceptionHandlerController {

    @Autowired
    private ProjectMemberService projectMemberService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<ProjectMemberReadDTO, Void> create(HttpSession session,
            @RequestBody ProjectMemberDTO projectMemberDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectMemberReadDTO projectMemberReadDTO = projectMemberService.create(getSessionUser(session), projectMemberDTO);
        logProcessingTimeFromStartTime(startTime, "create", projectMemberDTO.userId, projectMemberDTO.projectId);
        return new JsonContainer<ProjectMemberReadDTO, Void>(projectMemberReadDTO);
    }

    @PostMapping(value = "create-batch")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<ProjectMemberReadDTO, Void> createBatch(HttpSession session,
            @RequestBody ProjectMemberListDTO projectMemberListDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        List<ProjectMemberReadDTO> projectMemberDTOList = projectMemberService.createBatch(getSessionUser(session),
                projectMemberListDTO);
        logProcessingTimeFromStartTime(startTime, "createBatch", projectMemberListDTO.projectId);
        return new JsonContainer<ProjectMemberReadDTO, Void>(projectMemberDTOList);
    }

    @GetMapping(value = "/")
    public JsonContainer<ProjectMemberReadDTO, Void> read(HttpSession session,
            @RequestParam(name = "id") Long id) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectMemberReadDTO projectMemberDTO = projectMemberService.get(getSessionUser(session), id);
        logProcessingTimeFromStartTime(startTime, "read", id);
        return new JsonContainer<ProjectMemberReadDTO, Void>(projectMemberDTO);
    }

    @PutMapping(value = "/")
    public JsonContainer<ProjectMemberReadDTO, Void> update(HttpSession session,
            @RequestBody ProjectMemberDTO projectMemberDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectMemberReadDTO projectMemberReadDTO = projectMemberService.update(getSessionUser(session), projectMemberDTO);
        logProcessingTimeFromStartTime(startTime, "update", projectMemberDTO.id);
        return new JsonContainer<ProjectMemberReadDTO, Void>(projectMemberReadDTO);
    }

    @GetMapping(value = "list-by-project")
    public JsonContainer<ProjectMemberReadDTO, Void> listByProject(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) throws BadRequestException {
        long startTime = System.currentTimeMillis();
        List<ProjectMemberReadDTO> projectMemberDTOList = new ArrayList<ProjectMemberReadDTO>();
        if (projectId != null) {
            projectMemberDTOList = projectMemberService.listByProjectId(getSessionUser(session), projectId);
        } else if (projectUiId != null) {
            projectMemberDTOList = projectMemberService.listByProjectUiId(getSessionUser(session), projectUiId.strip());
        } else {
            throw new BadRequestException("Neither parameter 'project_id' nor parameter 'project_ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "listByProject", projectId, projectUiId);
        return new JsonContainer<ProjectMemberReadDTO, Void>(projectMemberDTOList);
    }

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session,
            @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        projectMemberService.delete(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.id);
        return new JsonContainer<Void, Void>();
    }

    @DeleteMapping(value = "leave")
    public JsonContainer<Void, Void> leave(HttpSession session,
            @RequestBody ProjectMemberDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        User user = getSessionUser(session);
        projectMemberService.leave(user, idDTO.projectId);
        logProcessingTimeFromStartTime(startTime, "leave", user, idDTO.projectId);
        return new JsonContainer<Void, Void>();
    }
}
