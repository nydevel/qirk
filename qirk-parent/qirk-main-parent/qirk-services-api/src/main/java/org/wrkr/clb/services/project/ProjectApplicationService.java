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
package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.project.ProjectApplicationDTO;
import org.wrkr.clb.services.dto.project.ProjectApplicationReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberPermissionsDTO;


@Validated
public interface ProjectApplicationService {

    public ProjectApplicationReadDTO create(User currentUser, @Valid ProjectApplicationDTO projectApplicationDTO)
            throws Exception;

    public List<ProjectApplicationReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId in ProjectApplicationService must not be null") Long projectId);

    public List<ProjectApplicationReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId in ProjectApplicationService must not be null") String projectUiId);

    public ProjectApplicationReadDTO reject(User currentUser, @Valid RejectDTO rejectDTO) throws Exception;

    public void accept(User currentUser, @Valid ProjectMemberPermissionsDTO applicationDTO) throws Exception;

    public List<ProjectApplicationReadDTO> listByUser(User currentUser);

    public void cancel(User currentUser,
            @NotNull(message = "id in ProjectApplicationService must not be null") Long id) throws Exception;
}
