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
package org.wrkr.clb.services.file;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.commons.fileupload.FileItem;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.AttachmentDTO;

@Validated
public interface FileService {

    public AttachmentDTO uploadAndCreateAttachment(User currentUser,
            @NotNull(message = "file must not be null") FileItem file,
            @NotNull(message = "taskId must not be null") Long taskId) throws Exception;

    public String uploadAndCreateTemporaryAttachment(User currentUser,
            @NotNull(message = "file must not be null") FileItem file,
            @NotNull(message = "projectId must not be null") Long projectId) throws Exception;

    public String getTemporaryLink(Attachment attachment) throws Exception;

    public String getErrorCodeFromException(Exception e) throws IOException;
}
