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
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.repo.project.JDBCProjectRepo;
import org.wrkr.clb.repo.project.task.TaskRepo;
import org.wrkr.clb.services.file.FileService;
import org.wrkr.clb.services.project.task.attachment.AttachmentService;
import org.wrkr.clb.services.project.task.attachment.TemporaryAttachmentService;
import org.wrkr.clb.services.security.ProjectSecurityService;

//@Service configured in clb-services-ctx.xml
@Validated
public abstract class DefaultFileService implements FileService {

    protected static final String TEMP_FILE_SUFFIX = ".clbtmp";
    protected static final int CHUNKED_UPLOAD_CHUNK_SIZE = 8192;

    protected static final String GET_FILE_ERROR_CODE_PARAMETER = "get_file_error_code";

    protected static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 500
        public static final String DROPBOX_AUTHORIZATION_FAILED = "DROPBOX_AUTHORIZATION_FAILED";
        public static final String DROPBOX_UPLOAD_FAILED = "DROPBOX_UPLOAD_FAILED";
        public static final String YANDEX_CLOUD_UPLOAD_FAILED = "YANDEX_CLOUD_UPLOAD_FAILED";
    }

    @Autowired
    protected JDBCProjectRepo projectRepo;

    @Autowired
    protected TaskRepo taskRepo;

    @Autowired
    protected AttachmentService attachmentService;

    @Autowired
    protected TemporaryAttachmentService temporaryAttachmentService;

    @Autowired
    protected ProjectSecurityService securityService;

    private String generateTemporaryFileName(String filename) {
        String fileUuid = UUID.randomUUID().toString();
        if (filename == null || filename.isBlank()) {
            return fileUuid;
        }

        int lastPointIndex = filename.lastIndexOf('.');
        if (lastPointIndex < 0) {
            return filename + "-" + fileUuid;
        }
        return filename.substring(0, lastPointIndex) + "-" + fileUuid + filename.substring(lastPointIndex);
    }

    protected File createTemporaryFile(FileItem file) throws Exception {
        String temporaryFilename = generateTemporaryFileName(file.getName());
        File temporaryFile = File.createTempFile(temporaryFilename, TEMP_FILE_SUFFIX);
        temporaryFile.deleteOnExit();

        InputStream uploadedStream = file.getInputStream();
        while (uploadedStream.available() > 0) {
            byte[] uploadedBytes = uploadedStream.readNBytes(CHUNKED_UPLOAD_CHUNK_SIZE);
            FileUtils.writeByteArrayToFile(temporaryFile, uploadedBytes, true);
        }
        uploadedStream.close();

        return temporaryFile;
    }
}
