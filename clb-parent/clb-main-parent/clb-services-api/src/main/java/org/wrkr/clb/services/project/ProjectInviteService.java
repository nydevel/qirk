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
import org.wrkr.clb.services.dto.project.ProjectInviteDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberPermissionsDTO;


@Validated
public interface ProjectInviteService {

    public ProjectInviteReadDTO create(User currentUser, @Valid ProjectInviteDTO projectInviteDTO) throws Exception;

    public ProjectInviteReadDTO get(User currentUser,
            @NotNull(message = "id in ProjectInviteService must not be null") Long id) throws Exception;

    public List<ProjectInviteReadDTO> listByUser(User currentUser);

    public ProjectInviteReadDTO accept(User currentUser,
            @NotNull(message = "id in ProjectInviteService must not be null") Long id) throws Exception;

    public ProjectInviteReadDTO reject(User currentUser, @Valid RejectDTO rejectDTO) throws Exception;

    public List<ProjectInviteReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId in ProjectInviteService must not be null") Long projectId);

    public List<ProjectInviteReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId in ProjectInviteService must not be null") String projectUiId);

    public void cancel(User currentUser,
            @NotNull(message = "id in ProjectInviteService must not be null") Long id) throws Exception;

    public void execute(User currentUser, @Valid ProjectMemberPermissionsDTO inviteDTO) throws Exception;
}
