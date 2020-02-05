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
package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnCreateBatch;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberUserDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberListDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberReadDTO;

@Validated
public interface ProjectMemberService {

    public ProjectMember create(
            @NotNull(message = "project in ProjectMemberService must not be null") Project project,
            @NotNull(message = "user in ProjectMemberService must not be null") User user,
            @Valid ProjectMemberDTO projectMemberDTO);

    @Validated(OnCreate.class)
    public ProjectMemberReadDTO create(User currentUser, @Valid ProjectMemberDTO projectMemberDTO) throws Exception;

    @Validated(OnCreateBatch.class)
    public List<ProjectMemberReadDTO> createBatch(User currentUser, @Valid ProjectMemberListDTO projectMemberListDTO)
            throws Exception;

    public ProjectMemberReadDTO get(User currentUser,
            @NotNull(message = "id in ProjectMemberService must not be null") Long id) throws Exception;

    @Validated(OnUpdate.class)
    public ProjectMemberReadDTO update(User currentUser, @Valid ProjectMemberDTO projectMemberDTO) throws Exception;

    public List<ProjectMemberReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId in ProjectMemberService must not be null") Long projectId);

    public List<ProjectMemberReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId in ProjectMemberService must not be null") String projectUiId);

    public List<ProjectMemberReadDTO> listByUser(User currentUser,
            @NotNull(message = "userId in ProjectMemberService must not be null") Long userId);

    public void delete(User currentUser,
            @NotNull(message = "id in ProjectMemberService must not be null") Long id) throws Exception;

    public void leave(User currentUser,
            @NotNull(message = "projectId in ProjectMemberService must not be null") Long projectId) throws Exception;

    public List<OrganizationMemberUserDTO> search(User currentUser,
            @NotNull(message = "prefix in ProjectMemberService must not be null") String prefix,
            @Valid IdOrUiIdDTO projectDTO, boolean meFirst)
            throws Exception;
}
