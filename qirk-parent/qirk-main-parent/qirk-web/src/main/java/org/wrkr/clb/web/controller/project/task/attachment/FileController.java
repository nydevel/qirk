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
package org.wrkr.clb.web.controller.project.task.attachment;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.view.RedirectView;
import org.wrkr.clb.services.dto.AttachmentDTO;
import org.wrkr.clb.services.dto.UuidDTO;
import org.wrkr.clb.services.dto.meta.ExternalUuidDTO;
import org.wrkr.clb.services.file.FileService;
import org.wrkr.clb.services.file.YandexCloudFileService;
import org.wrkr.clb.services.project.task.attachment.AttachmentFileService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.RequestEntityTooLargeException;
import org.wrkr.clb.web.controller.BaseController;
import org.wrkr.clb.web.json.JsonContainer;

@Controller
@RequestMapping(path = "file")
public class FileController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private static final int FILENAME_MAX_LENGTH = 511;

    private static class UploadFieldName {
        private static final String TASK_ID = "task_id";
        private static final String PROJECT_ID = "project_id";
        private static final String UUID = "uuid";
        private static final String FILE = "file";
    }

    private static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 400
        public static final String TOO_MANY_FILES = "TOO_MANY_FILES";
        public static final String NO_FILES = "NO_FILES";
        public static final String FILENAME_TOO_LONG = "FILENAME_TOO_LONG";

        // 413
        public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    }

    @Autowired
    private YandexCloudFileService yandexCloudFileService;

    @Autowired
    private AttachmentFileService attachmentService;

    @Autowired
    @Qualifier("maxFileSize")
    private Long fileUploadMaxSize;

    private JsonContainer<?, ExternalUuidDTO> upload(FileService fileService,
            HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String externalUuid = null;
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

            Long taskId = null;
            Long projectId = null;
            FileItem file = null;

            for (FileItem item : items) {

                switch (item.getFieldName()) {
                    case UploadFieldName.TASK_ID:
                        taskId = getLongParameter(item, UploadFieldName.TASK_ID);
                        break;

                    case UploadFieldName.PROJECT_ID:
                        projectId = getLongParameter(item, UploadFieldName.PROJECT_ID);
                        break;

                    case UploadFieldName.UUID:
                        externalUuid = item.getString();
                        break;

                    case UploadFieldName.FILE:
                        if (file != null) {
                            throw new BadRequestException(JsonStatusCode.TOO_MANY_FILES, "Too many files to upload.");
                        }
                        file = item;
                        break;
                }
            }

            if (taskId == null && projectId == null) {
                throw new BadRequestException("Neither parameter '" + UploadFieldName.TASK_ID + "' " +
                        "nor parameter '" + UploadFieldName.PROJECT_ID + "' is present.");
            }
            if (file == null) {
                throw new BadRequestException(JsonStatusCode.NO_FILES, "No files to upload.");
            }
            if (file.getSize() > fileUploadMaxSize) {
                throw new RequestEntityTooLargeException(JsonStatusCode.FILE_TOO_LARGE, "File too large.");
            }
            if (file.getName().length() > FILENAME_MAX_LENGTH) {
                throw new BadRequestException(JsonStatusCode.FILENAME_TOO_LONG,
                        "Filename size must be no more than " + FILENAME_MAX_LENGTH);
            }

            if (taskId == null) {
                String uuid = fileService.uploadAndCreateTemporaryAttachment(getSessionUser(session), file, projectId);
                response.setStatus(HttpServletResponse.SC_CREATED);
                return new JsonContainer<UuidDTO, ExternalUuidDTO>(new UuidDTO(uuid), new ExternalUuidDTO(externalUuid));
            }

            AttachmentDTO dto = fileService.uploadAndCreateAttachment(getSessionUser(session), file, taskId);
            response.setStatus(HttpServletResponse.SC_CREATED);
            return new JsonContainer<AttachmentDTO, ExternalUuidDTO>(dto, new ExternalUuidDTO(externalUuid));

        } catch (SecurityException e) {
            LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return JsonContainer.fromCodeAndReason(JsonStatusCode.FORBIDDEN,
                    "You do not have permission to perform this action.", new ExternalUuidDTO(externalUuid));

        } catch (ApplicationException e) {
            LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
            response.setStatus(e.getHttpStatus());
            return JsonContainer.fromApplicationException(e, new ExternalUuidDTO(externalUuid));

        } catch (Exception e) {
            LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return JsonContainer.fromCodeAndReason(JsonStatusCode.INTERNAL_SERVER_ERROR,
                    "A server error occurred.", new ExternalUuidDTO(externalUuid));
        }
    }

    @PostMapping(value = "upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public JsonContainer<?, ExternalUuidDTO> upload(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) {
        return upload(yandexCloudFileService, request, response, session);
    }

    @GetMapping(value = "get/{id}/{filename}")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public RedirectView get(HttpServletResponse response, HttpSession session,
            @PathVariable("id") Long id,
            @SuppressWarnings("unused") @PathVariable("filename") String filename) throws IOException {
        String redirectURL = attachmentService.getTemporaryLink(getSessionUser(session), id);
        if (redirectURL == null) {
            LOG.warn("File with id " + id + " is not found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return new RedirectView(redirectURL);
    }
}
