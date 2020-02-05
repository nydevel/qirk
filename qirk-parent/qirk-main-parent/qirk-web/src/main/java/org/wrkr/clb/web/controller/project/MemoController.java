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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.project.MemoDTO;
import org.wrkr.clb.services.dto.project.MemoReadDTO;
import org.wrkr.clb.services.project.MemoService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "memo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MemoController extends BaseExceptionHandlerController {

    @Autowired
    private MemoService memoService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<MemoReadDTO, Void> create(HttpSession session,
            @RequestBody MemoDTO memoDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        MemoReadDTO memoReadDTO = memoService.create(getSessionUser(session), memoDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<MemoReadDTO, Void>(memoReadDTO);
    }

    @GetMapping(value = "list")
    public JsonContainer<MemoReadDTO, Void> list(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) throws BadRequestException {
        long startTime = System.currentTimeMillis();
        List<MemoReadDTO> memoDTOList = new ArrayList<MemoReadDTO>();
        if (projectId != null) {
            memoDTOList = memoService.listByProjectId(getSessionUser(session), projectId);
        } else if (projectUiId != null) {
            memoDTOList = memoService.listByProjectUiId(getSessionUser(session), projectUiId.strip());
        } else {
            throw new BadRequestException("Neither parameter 'project_id' nor parameter 'project_ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "list", projectId, projectUiId);
        return new JsonContainer<MemoReadDTO, Void>(memoDTOList);
    }

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session, @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        memoService.delete(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
