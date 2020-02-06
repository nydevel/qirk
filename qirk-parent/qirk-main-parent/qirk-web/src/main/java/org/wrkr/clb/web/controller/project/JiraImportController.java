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

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.dto.project.imprt.ImportStatusDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectMatchDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectImportDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraUploadDTO;
import org.wrkr.clb.services.project.imprt.jira.JiraImportService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "jira-import", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class JiraImportController extends BaseExceptionHandlerController {

    private static final int FILENAME_MAX_LENGTH = 511;

    private static class UploadFieldName {
        private static final String FILE = "file";
    }

    private static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 400
        public static final String TOO_MANY_FILES = "TOO_MANY_FILES";
        public static final String NO_FILES = "NO_FILES";
        public static final String FILENAME_TOO_LONG = "FILENAME_TOO_LONG";
    }

    @Autowired
    private JiraImportService projectImportService;

    @PostMapping(value = "upload")
    public JsonContainer<JiraUploadDTO, Void> upload(HttpServletRequest request, HttpSession session) throws Exception {
        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

        FileItem file = null;

        for (FileItem item : items) {

            if (UploadFieldName.FILE.equals(item.getFieldName())) {
                if (file != null) {
                    throw new BadRequestException(JsonStatusCode.TOO_MANY_FILES, "Too many files to upload.");
                }
                file = item;
            }
        }

        if (file == null) {
            throw new BadRequestException(JsonStatusCode.NO_FILES, "No files to upload.");
        }
        if (file.getName().length() > FILENAME_MAX_LENGTH) {
            throw new BadRequestException(JsonStatusCode.FILENAME_TOO_LONG,
                    "Filename size must be no more than " + FILENAME_MAX_LENGTH);
        }

        JiraUploadDTO dto = projectImportService.uploadJiraImportFile(getSessionUser(session),
                file);
        return new JsonContainer<JiraUploadDTO, Void>(dto);
    }

    @GetMapping(value = "list-uploads")
    public JsonContainer<JiraUploadDTO, Void> listUploads(HttpSession session) throws Exception {
        long startTime = System.currentTimeMillis();
        List<JiraUploadDTO> dtoList = projectImportService.listUploads(getSessionUser(session));
        logProcessingTimeFromStartTime(startTime, "listUploads");
        return new JsonContainer<JiraUploadDTO, Void>(dtoList);
    }

    @GetMapping(value = "list-projects")
    public JsonContainer<JiraProjectDTO, Void> listProjects(HttpSession session,
            @RequestParam(name = "timestamp") long timestamp) throws Exception {
        long startTime = System.currentTimeMillis();
        List<JiraProjectDTO> projectDTOList = projectImportService.listProjects(getSessionUser(session),
                timestamp);
        logProcessingTimeFromStartTime(startTime, "listProjects", timestamp);
        return new JsonContainer<JiraProjectDTO, Void>(projectDTOList);
    }

    @GetMapping(value = "list-projects-data")
    public JsonContainer<JiraProjectMatchDTO, Void> listProjectsData(HttpSession session,
            @RequestParam(name = "timestamp") long timestamp,
            @RequestParam(name = "project_id") Set<String> projectIds) throws Exception {
        long startTime = System.currentTimeMillis();
        JiraProjectMatchDTO dto = projectImportService.listProjectsData(
                getSessionUser(session), timestamp, projectIds);
        logProcessingTimeFromStartTime(startTime, "listProjectsData", timestamp);
        return new JsonContainer<JiraProjectMatchDTO, Void>(dto);
    }

    @PostMapping(value = "/")
    public JsonContainer<ImportStatusDTO, Void> importProjects(HttpSession session,
            @RequestBody JiraProjectImportDTO importDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        List<ImportStatusDTO> projectDTOList = projectImportService.importProjects(getSessionUser(session), importDTO);
        logProcessingTimeFromStartTime(startTime, "importProjects");
        return new JsonContainer<ImportStatusDTO, Void>(projectDTOList);
    }
}
