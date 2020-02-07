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
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.api.yandexcloud.YandexCloudApiService;
import org.wrkr.clb.services.dto.AttachmentDTO;
import org.wrkr.clb.services.file.YandexCloudFileService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;

import com.amazonaws.services.s3.model.AmazonS3Exception;

//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultYandexCloudFileService extends DefaultFileService implements YandexCloudFileService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultYandexCloudFileService.class);

    private static final String FILE_PATH_PREFIX = "attachment";

    @Autowired
    private YandexCloudApiService yandexCloudService;

    /**
     * 
     * @return path to yandex cloud file
     */
    private String upload(FileItem file, String folderPath) throws Exception {
        File temporaryFile = createTemporaryFile(file);
        String yandexCloudFilename = ((file.getName() == null || file.getName().isEmpty())
                ? temporaryFile.getName()
                : file.getName());
        String yandexCloudFilePath = folderPath + yandexCloudFilename;
        try {
            yandexCloudService.upload(yandexCloudFilePath, temporaryFile);
            return yandexCloudFilePath;
        } catch (Exception e) {
            throw new ApplicationException(JsonStatusCode.YANDEX_CLOUD_UPLOAD_FAILED,
                    "A server error occurred during uploading to yandex cloud.", e);
        }
    }

    private void deleteAndThrowException(String yandexCloudPath, Exception cause) throws Exception {
        yandexCloudService.deleteFile(yandexCloudPath);
        throw new ApplicationException("A server error occurred during creating attachment.", cause);
    }

    private String generatePathPrefix(Project project, String identificator) {
        return "/" + FILE_PATH_PREFIX + "/" +
                project.getId() + "-" + project.getUiId() + "/" +
                identificator + "/" +
                System.currentTimeMillis() + "/";
    }

    @Override
    public String generatePathPrefix(Task task) {
        return generatePathPrefix(task.getProject(), task.getNumber().toString());
    }

    private String generatePathPrefix(Project project) {
        return generatePathPrefix(project, UUID.randomUUID().toString());
    }

    @Override
    public AttachmentDTO uploadAndCreateAttachment(User currentUser, FileItem file, Long taskId) throws Exception {
        // security
        securityService.authzCanUpdateTask(currentUser, taskId);
        // security

        Task task = taskRepo.getByIdAndFetchProject(taskId);
        if (task == null) {
            throw new NotFoundException("Task");
        }

        String yandexCloudPath = upload(file, generatePathPrefix(task));

        try {
            return attachmentService.create(yandexCloudPath, task);
        } catch (Exception e) {
            deleteAndThrowException(yandexCloudPath, e);
        }
        return null;
    }

    @Override
    public String uploadAndCreateTemporaryAttachment(User currentUser, FileItem file, Long projectId)
            throws Exception {
        // security
        securityService.authzCanCreateTask(currentUser, projectId);
        // security

        Project project = projectRepo.getUiIdById(projectId);
        if (project == null) {
            throw new NotFoundException("Project");
        }

        String yandexCloudPath = upload(file, generatePathPrefix(project));

        try {
            return temporaryAttachmentService.create(yandexCloudPath, project);
        } catch (Exception e) {
            deleteAndThrowException(yandexCloudPath, e);
        }
        return null;
    }

    @Override
    public String getTemporaryLink(Attachment attachment) throws Exception {
        return yandexCloudService.getTemporaryLink(attachment.getPath());
    }

    @Override
    public String getErrorCodeFromException(Exception e) throws IOException {
        if (!(e instanceof AmazonS3Exception)) {
            LOG.error("Exception caught at file service", e);
            return JsonStatusCode.INTERNAL_SERVER_ERROR;
        }

        AmazonS3Exception amazonS3Exception = (AmazonS3Exception) e;

        if (amazonS3Exception.getStatusCode() == HttpServletResponse.SC_NOT_FOUND) {
            return JsonStatusCode.NOT_FOUND;
        }

        LOG.error("HTTP client error: code " + amazonS3Exception.getErrorCode() + " (" + amazonS3Exception.getStatusCode() +
                "); message " + amazonS3Exception.getErrorMessage());
        return JsonStatusCode.INTERNAL_SERVER_ERROR;
    }
}
