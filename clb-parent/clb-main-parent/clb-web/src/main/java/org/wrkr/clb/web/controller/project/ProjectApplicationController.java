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
import org.wrkr.clb.services.dto.project.ProjectApplicationDTO;
import org.wrkr.clb.services.dto.project.ProjectApplicationReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberPermissionsDTO;
import org.wrkr.clb.services.project.ProjectApplicationService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "project-application", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ProjectApplicationController extends BaseExceptionHandlerController {

    @Autowired
    private ProjectApplicationService projectApplicationService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<ProjectApplicationReadDTO, Void> create(HttpSession session,
            @RequestBody ProjectApplicationDTO projectApplicationDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectApplicationReadDTO projectApplicationReadDTO = projectApplicationService.create(getSessionUser(session),
                projectApplicationDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<ProjectApplicationReadDTO, Void>(projectApplicationReadDTO);
    }

    @GetMapping(value = "list-by-project")
    public JsonContainer<ProjectApplicationReadDTO, Void> listByProject(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) throws BadRequestException {
        long startTime = System.currentTimeMillis();
        List<ProjectApplicationReadDTO> projectApplicationDTOList = new ArrayList<ProjectApplicationReadDTO>();
        if (projectId != null) {
            projectApplicationDTOList = projectApplicationService.listByProjectId(getSessionUser(session), projectId);
        } else if (projectUiId != null) {
            projectApplicationDTOList = projectApplicationService.listByProjectUiId(getSessionUser(session), projectUiId.strip());
        } else {
            throw new BadRequestException("Neither parameter 'project_id' nor parameter 'project_ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "listByProject", projectId, projectUiId);
        return new JsonContainer<ProjectApplicationReadDTO, Void>(projectApplicationDTOList);
    }

    @PutMapping(value = "reject")
    public JsonContainer<ProjectApplicationReadDTO, Void> reject(HttpSession session,
            @RequestBody RejectDTO rejectDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectApplicationReadDTO projectApplicationDTO = projectApplicationService.reject(getSessionUser(session),
                rejectDTO);
        logProcessingTimeFromStartTime(startTime, "reject", rejectDTO.id, rejectDTO.reported);
        return new JsonContainer<ProjectApplicationReadDTO, Void>(projectApplicationDTO);
    }

    @PostMapping(value = "accept")
    public JsonContainer<Void, Void> execute(HttpSession session,
            @RequestBody ProjectMemberPermissionsDTO executeDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        projectApplicationService.accept(getSessionUser(session), executeDTO);
        logProcessingTimeFromStartTime(startTime, "execute", executeDTO.id);
        return new JsonContainer<Void, Void>();
    }

    @GetMapping(value = "list-by-user")
    public JsonContainer<ProjectApplicationReadDTO, Void> listByUser(HttpSession session) {
        long startTime = System.currentTimeMillis();
        List<ProjectApplicationReadDTO> projectApplicationDTOList = projectApplicationService
                .listByUser(getSessionUser(session));
        logProcessingTimeFromStartTime(startTime, "listByUser");
        return new JsonContainer<ProjectApplicationReadDTO, Void>(projectApplicationDTOList);
    }

    @DeleteMapping(value = "cancel")
    public JsonContainer<Void, Void> cancel(HttpSession session,
            @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        projectApplicationService.cancel(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "cancel", idDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
