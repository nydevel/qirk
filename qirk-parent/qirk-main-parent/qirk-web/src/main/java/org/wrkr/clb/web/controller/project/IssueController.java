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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.project.IssueDTO;
import org.wrkr.clb.services.dto.project.IssueReadDTO;
import org.wrkr.clb.services.project.IssueService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "issue", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class IssueController extends BaseExceptionHandlerController {

    @Autowired
    private IssueService issueService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<IssueReadDTO, Void> create(HttpSession session,
            @RequestBody IssueDTO issueDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        IssueReadDTO issueReadDTO = issueService.create(getSessionUser(session), issueDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<IssueReadDTO, Void>(issueReadDTO);
    }

    @PutMapping(value = "/")
    public JsonContainer<IssueReadDTO, Void> update(HttpSession session,
            @RequestBody IssueDTO issueDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        IssueReadDTO issueReadDTO = issueService.update(getSessionUser(session), issueDTO);
        logProcessingTimeFromStartTime(startTime, "update", issueDTO.id);
        return new JsonContainer<IssueReadDTO, Void>(issueReadDTO);
    }

    @GetMapping(value = "/")
    public JsonContainer<IssueReadDTO, Void> read(HttpSession session,
            @RequestParam(name = "id") Long id) throws Exception {
        long startTime = System.currentTimeMillis();
        IssueReadDTO issueDTO = issueService.get(getSessionUser(session), id);
        logProcessingTimeFromStartTime(startTime, "read", id);
        return new JsonContainer<IssueReadDTO, Void>(issueDTO);
    }

    @GetMapping(value = "list")
    public JsonContainer<IssueReadDTO, Void> list(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) throws ApplicationException {
        long startTime = System.currentTimeMillis();
        List<IssueReadDTO> issueDTOList = new ArrayList<IssueReadDTO>();
        if (projectId != null) {
            issueDTOList = issueService.listByProjectId(getSessionUser(session), projectId);
        } else if (projectUiId != null) {
            issueDTOList = issueService.listByProjectUiId(getSessionUser(session), projectUiId.strip());
        } else {
            throw new BadRequestException("Neither parameter 'project_id' nor parameter 'project_ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "list", projectId, projectUiId);
        return new JsonContainer<IssueReadDTO, Void>(issueDTOList);
    }

    @GetMapping(value = "chat-token")
    public JsonContainer<ChatPermissionsDTO, Void> getChatToken(HttpSession session,
            @RequestParam(name = "id") Long id) throws Exception {
        long startTime = System.currentTimeMillis();
        ChatPermissionsDTO tokenDTO = issueService.getChatToken(getSessionUser(session), id);
        logProcessingTimeFromStartTime(startTime, "getChatToken", id);
        return new JsonContainer<ChatPermissionsDTO, Void>(tokenDTO);
    }
}
