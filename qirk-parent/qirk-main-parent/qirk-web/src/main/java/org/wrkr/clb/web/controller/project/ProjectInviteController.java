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
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberPermissionsDTO;
import org.wrkr.clb.services.project.ProjectInviteService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "project-invite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ProjectInviteController extends BaseExceptionHandlerController {

    @Autowired
    private ProjectInviteService projectInviteService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<ProjectInviteReadDTO, Void> create(HttpSession session,
            @RequestBody ProjectInviteDTO projectInviteDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectInviteReadDTO projectInviteReadDTO = projectInviteService.create(getSessionUser(session), projectInviteDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<ProjectInviteReadDTO, Void>(projectInviteReadDTO);
    }

    @GetMapping(value = "/")
    public JsonContainer<ProjectInviteReadDTO, Void> read(HttpSession session,
            @RequestParam(name = "id") Long id) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectInviteReadDTO projectInviteDTO = projectInviteService.get(getSessionUser(session), id);
        logProcessingTimeFromStartTime(startTime, "read", id);
        return new JsonContainer<ProjectInviteReadDTO, Void>(projectInviteDTO);
    }

    @GetMapping(value = "list-by-user")
    public JsonContainer<ProjectInviteReadDTO, Void> listByUser(HttpSession session) {
        long startTime = System.currentTimeMillis();
        List<ProjectInviteReadDTO> projectInviteDTOList = projectInviteService.listByUser(getSessionUser(session));
        logProcessingTimeFromStartTime(startTime, "listByUser");
        return new JsonContainer<ProjectInviteReadDTO, Void>(projectInviteDTOList);
    }

    @PutMapping(value = "accept")
    public JsonContainer<ProjectInviteReadDTO, Void> accept(HttpSession session,
            @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectInviteReadDTO projectInviteDTO = projectInviteService.accept(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "accept", idDTO.id);
        return new JsonContainer<ProjectInviteReadDTO, Void>(projectInviteDTO);
    }

    @PutMapping(value = "reject")
    public JsonContainer<ProjectInviteReadDTO, Void> reject(HttpSession session,
            @RequestBody RejectDTO rejectDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectInviteReadDTO projectInviteDTO = projectInviteService.reject(getSessionUser(session), rejectDTO);
        logProcessingTimeFromStartTime(startTime, "reject", rejectDTO.id, rejectDTO.reported);
        return new JsonContainer<ProjectInviteReadDTO, Void>(projectInviteDTO);
    }

    @GetMapping(value = "list-by-project")
    public JsonContainer<ProjectInviteReadDTO, Void> listByProject(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) throws BadRequestException {
        long startTime = System.currentTimeMillis();
        List<ProjectInviteReadDTO> projectInviteDTOList = new ArrayList<ProjectInviteReadDTO>();
        if (projectId != null) {
            projectInviteDTOList = projectInviteService.listByProjectId(getSessionUser(session), projectId);
        } else if (projectUiId != null) {
            projectInviteDTOList = projectInviteService.listByProjectUiId(getSessionUser(session), projectUiId.strip());
        } else {
            throw new BadRequestException("Neither parameter 'project_id' nor parameter 'project_ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "listByProject", projectId, projectUiId);
        return new JsonContainer<ProjectInviteReadDTO, Void>(projectInviteDTOList);
    }

    @DeleteMapping(value = "cancel")
    public JsonContainer<Void, Void> cancel(HttpSession session,
            @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        projectInviteService.cancel(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "cancel", idDTO.id);
        return new JsonContainer<Void, Void>();
    }

    @PostMapping(value = "execute")
    public JsonContainer<Void, Void> execute(HttpSession session,
            @RequestBody ProjectMemberPermissionsDTO executeDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        projectInviteService.execute(getSessionUser(session), executeDTO);
        logProcessingTimeFromStartTime(startTime, "execute", executeDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
