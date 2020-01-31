/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.services.project.task;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.task.TaskHashtagDTO;

@Validated
public interface TaskHashtagService {

    public List<TaskHashtagDTO> searchByProjectId(User currentUser,
            @NotNull(message = "prefix must not be null") String prefix,
            @NotNull(message = "projectId must not be null") Long projectId,
            boolean includeUsed);

    public List<TaskHashtagDTO> searchByProjectUiId(User currentUser,
            @NotNull(message = "prefix must not be null") String prefix,
            @NotNull(message = "projectUiId must not be null") String projectUiId,
            boolean includeUsed);

    public void delete(User currentUser, @NotNull(message = "id must not be null") Long id) throws Exception;
}
