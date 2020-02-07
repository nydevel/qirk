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
import org.wrkr.clb.services.dto.AttachmentDTO;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.project.task.AttachmentCreateDTO;
import org.wrkr.clb.services.file.AttachmentService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "attachment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AttachmentController extends BaseExceptionHandlerController {

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<AttachmentDTO, Void> createFromTemporary(HttpSession session,
            @RequestBody AttachmentCreateDTO createDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        List<AttachmentDTO> attachmentDTOList = attachmentService.createFromTemporary(getSessionUser(session), createDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<AttachmentDTO, Void>(attachmentDTOList);
    }

    @GetMapping(value = "list")
    public JsonContainer<AttachmentDTO, Void> list(HttpSession session,
            @RequestParam("task_id") Long taskId) throws Exception {
        long startTime = System.currentTimeMillis();
        List<AttachmentDTO> attachmentDTOList = attachmentService.listByTask(getSessionUser(session), taskId);
        logProcessingTimeFromStartTime(startTime, "list", taskId);
        return new JsonContainer<AttachmentDTO, Void>(attachmentDTOList);
    }

    /*
     * @GetMapping(value = "thumbnail", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE,
     * MediaType.IMAGE_PNG_VALUE })
     * public String getThumbnail(HttpSession session,
     * 
     * @RequestParam("id") Long id) throws Exception {
     * long startTime = System.currentTimeMillis();
     * String thumbnail = attachmentService.getThumbnail(getSessionUser(session), id);
     * logProcessingTimeFromStartTime(startTime, "getThumbnail", id);
     * return thumbnail;
     * }
     */

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session, @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        attachmentService.delete(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
