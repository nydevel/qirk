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
package org.wrkr.clb.services.project.task.attachment.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.task.attachment.AttachmentRepo;
import org.wrkr.clb.services.file.FileService;
import org.wrkr.clb.services.file.YandexCloudFileService;
import org.wrkr.clb.services.project.task.attachment.AttachmentFileService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.http.JsonStatusCode;

@Service
@Validated
public class DefaultAttachmentFileService implements AttachmentFileService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAttachmentFileService.class);

    protected static final String GET_FILE_ERROR_CODE_PARAMETER = "get_file_error_code";

    @Autowired
    private AttachmentRepo attachmentRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private YandexCloudFileService yandexCloudFileService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public String getTemporaryLink(User currentUser, Long id) {
        Attachment attachment = attachmentRepo.getNotDeletedByIdAndFetchDropboxSettingsAndTask(id);
        if (attachment == null) {
            return null;
        }

        try {
            // security
            securityService.authzCanReadTask(currentUser, attachment.getTask().getId());
            // security
        } catch (SecurityException e) {
            return null;
        }

        FileService fileService = yandexCloudFileService;
        String errorCode = JsonStatusCode.INTERNAL_SERVER_ERROR;
        try {
            return fileService.getTemporaryLink(attachment);
        } catch (Exception e) {
            try {
                errorCode = fileService.getErrorCodeFromException(e);
            } catch (IOException e2) {
                LOG.error("Exception caught at file service", e2);
            }
        }

        return fileService.generateTaskUrl(attachment.getTask()) + "?" + GET_FILE_ERROR_CODE_PARAMETER + "=" + errorCode;
    }
}
