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
package org.wrkr.clb.services.file.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.model.project.DropboxSettings;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.Attachment;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.api.dropbox.DropboxApiService;
import org.wrkr.clb.services.dto.AttachmentDTO;
import org.wrkr.clb.services.file.DropboxFileService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;


//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultDropboxFileService extends DefaultFileService implements DropboxFileService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDropboxFileService.class);

    private static final String FILE_PATH_PREFIX = "qirk";

    @Autowired
    private DropboxApiService dropboxService;

    /**
     * 
     * @return path to dropbox file
     */
    @Deprecated
    private String upload(FileItem file, DropboxSettings dropboxSettings, String path) throws Exception {
        if (dropboxSettings == null) {
            throw new NotFoundException("Dropbox settings");
        }
        String token = dropboxSettings.getToken();

        File temporaryFile = createTemporaryFile(file);
        String dropboxFilename = ((file.getName() == null || file.getName().isEmpty())
                ? temporaryFile.getName()
                : file.getName());
        try {
            return dropboxService.upload(token, path + dropboxFilename, temporaryFile);

        } catch (Exception e) {
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientError = (HttpClientErrorException) e;
                if (httpClientError.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new ApplicationException(JsonStatusCode.DROPBOX_AUTHORIZATION_FAILED, "Dropbox authorization failed.",
                            httpClientError);
                }

                LOG.error("HTTP client error: code " + httpClientError.getRawStatusCode() +
                        "; response " + httpClientError.getResponseBodyAsString());
            }

            throw new ApplicationException(JsonStatusCode.DROPBOX_UPLOAD_FAILED,
                    "A server error occurred during uploading to dropbox.", e);
        }
    }

    private void deleteAndThrowException(DropboxSettings dropboxSettings, String dropboxPath, Exception cause)
            throws Exception {
        dropboxService.delete(dropboxSettings.getToken(), dropboxPath);
        throw new ApplicationException("A server error occurred during creating attachment.", cause);
    }

    private String generatePathPrefix(Task task) {
        return "/" + FILE_PATH_PREFIX + "/" + task.getProject().getName() + "/" + task.getNumber() + "/";
    }

    private String generatePathPrefix(Project project) {
        return "/" + FILE_PATH_PREFIX + "/" + project.getName() + "/" + UUID.randomUUID() + "/";
    }

    @Deprecated
    @Override
    public AttachmentDTO uploadAndCreateAttachment(User currentUser, FileItem file, Long taskId) throws Exception {
        // security
        securityService.authzCanUpdateTask(currentUser, taskId);
        // security

        Task task = taskRepo.getByIdForDropboxAndFetchProject(taskId);
        if (task == null) {
            throw new NotFoundException("Task");
        }

        DropboxSettings dropboxSettings = task.getProject().getDropboxSettings();
        if (dropboxSettings == null) {
            dropboxSettings = task.getProject().getOrganization().getDropboxSettings();
        }

        String dropboxPath = upload(file, dropboxSettings, generatePathPrefix(task));

        try {
            return attachmentService.create(dropboxPath, task, dropboxSettings);
        } catch (Exception e) {
            deleteAndThrowException(dropboxSettings, dropboxPath, e);
        }
        return null;
    }

    @Deprecated
    @Override
    public String uploadAndCreateTemporaryAttachment(User currentUser, FileItem file, Long projectId)
            throws Exception {
        // security
        securityService.authzCanCreateTask(currentUser, projectId);
        // security

        Project project = projectRepo.getByIdAndFetchDropboxSettings(projectId);
        if (project == null) {
            throw new NotFoundException("Project");
        }

        DropboxSettings dropboxSettings = project.getDropboxSettings();
        if (dropboxSettings == null) {
            dropboxSettings = project.getOrganization().getDropboxSettings();
        }

        String dropboxPath = upload(file, dropboxSettings, generatePathPrefix(project));

        try {
            return temporaryAttachmentService.create(dropboxPath, project, dropboxSettings);
        } catch (Exception e) {
            deleteAndThrowException(dropboxSettings, dropboxPath, e);
        }
        return null;
    }

    @Override
    public String getTemporaryLink(Attachment attachment) throws Exception {
        return dropboxService.getTemporaryLink(attachment.getDropboxSettings().getToken(), attachment.getPath());
    }

    @Override
    public String getErrorCodeFromException(Exception e) throws IOException {
        if (!(e instanceof HttpClientErrorException)) {
            LOG.error("Exception caught at file service", e);
            return JsonStatusCode.INTERNAL_SERVER_ERROR;
        }

        HttpClientErrorException httpClientError = (HttpClientErrorException) e;
        if (httpClientError.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return JsonStatusCode.DROPBOX_AUTHORIZATION_FAILED;
        }

        Map<String, Object> response = JsonUtils.<Object>convertJsonToMap(httpClientError.getResponseBodyAsString());
        String error = (String) response.get("error_summary");
        if (error != null && error.startsWith("path/")) {
            return JsonStatusCode.NOT_FOUND;
        }

        LOG.error("HTTP client error: code " + httpClientError.getRawStatusCode() +
                "; response " + httpClientError.getResponseBodyAsString());
        return JsonStatusCode.INTERNAL_SERVER_ERROR;
    }
}
