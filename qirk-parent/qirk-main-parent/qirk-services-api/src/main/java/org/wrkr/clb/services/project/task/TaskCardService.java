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
package org.wrkr.clb.services.project.task;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.RecordVersionDTO;
import org.wrkr.clb.services.dto.project.MoveToRoadDTO;
import org.wrkr.clb.services.dto.project.task.TaskCardDTO;
import org.wrkr.clb.services.dto.project.task.TaskCardReadDTO;


@Validated
public interface TaskCardService {

    public TaskCardReadDTO create(User currentUser, @Valid TaskCardDTO cardDTO) throws Exception;

    public TaskCardReadDTO update(User currentUser, @Valid TaskCardDTO cardDTO) throws Exception;

    public void move(User currentUser, @Valid MoveToRoadDTO moveDTO) throws Exception;

    public List<TaskCardReadDTO> list(User currentUser,
            @NotNull(message = "projectId in TaskCardService must not be null") Long projectId);

    public List<TaskCardReadDTO> list(User currentUser,
            @NotNull(message = "projectUiId in TaskCardService must not be null") String projectUiId);

    public void archive(User currentUser, @Valid RecordVersionDTO dto) throws Exception;

    public void archiveBatchByRoadId(Long roadId);

    public List<TaskCardReadDTO> listArchive(User currentUser,
            @NotNull(message = "projectId in TaskCardService must not be null") Long projectId);

    public List<TaskCardReadDTO> listArchive(User currentUser,
            @NotNull(message = "projectUiId in TaskCardService must not be null") String projectUiId);

    public void delete(User currentUser,
            @NotNull(message = "projectId in TaskCardService must not be null") Long id) throws Exception;
}
