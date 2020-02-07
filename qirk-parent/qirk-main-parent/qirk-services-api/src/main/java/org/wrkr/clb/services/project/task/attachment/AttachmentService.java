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
package org.wrkr.clb.services.project.task.attachment;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.AttachmentDTO;
import org.wrkr.clb.services.dto.project.task.AttachmentCreateDTO;

@Validated
public interface AttachmentService {

    public AttachmentDTO create(
            @NotNull(message = "externalPath create must not be null") String externalPath,
            @NotNull(message = "task create must not be null") Task task);

    public List<AttachmentDTO> createFromTemporary(User currentUser, @Valid AttachmentCreateDTO createDTO) throws Exception;

    public List<AttachmentDTO> listByTask(User currentUser,
            @NotNull(message = "taskId listByTask must not be null") Long taskId);

    public void delete(User currentUser,
            @NotNull(message = "id delete must not be null") Long id) throws Exception;

    // public String getThumbnail(User currentUser,
    // @NotNull(message = "id delete must not be null") Long id) throws Exception;
}
