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
import org.wrkr.clb.common.validation.groups.OnCreateByEmail;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.TokenRejectDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteReadDTO;
import org.wrkr.clb.services.dto.user.TokenRegisterDTO;


@Validated
public interface GrantedPermissionsProjectInviteService {

    @Validated(OnCreate.class)
    public GrantedPermissionsProjectInviteReadDTO create(User currentUser,
            @Valid GrantedPermissionsProjectInviteDTO projectInviteDTO) throws Exception;

    @Validated(OnCreateByEmail.class)
    public GrantedPermissionsProjectInviteReadDTO createByEmail(User currentUser,
            @Valid GrantedPermissionsProjectInviteDTO projectInviteDTO)
            throws Exception;

    @Validated(OnUpdate.class)
    public GrantedPermissionsProjectInviteReadDTO update(User currentUser,
            @Valid GrantedPermissionsProjectInviteDTO projectInviteDTO) throws Exception;

    public GrantedPermissionsProjectInviteReadDTO get(User currentUser,
            @NotNull(message = "id in GrantedPermissionsProjectInviteService must not be null") Long id)
            throws Exception;

    public List<GrantedPermissionsProjectInviteReadDTO> listByUser(User currentUser);

    public void accept(User currentUser,
            @NotNull(message = "id in GrantedPermissionsProjectInviteService must not be null") Long id)
            throws Exception;

    public void acceptByToken(@Valid TokenRegisterDTO dto) throws Exception;

    public GrantedPermissionsProjectInviteReadDTO reject(User currentUser, @Valid RejectDTO rejectDTO) throws Exception;

    public GrantedPermissionsProjectInviteReadDTO rejectByToken(@Valid TokenRejectDTO rejectDTO) throws Exception;

    public List<GrantedPermissionsProjectInviteReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId in GrantedPermissionsProjectInviteService must not be null") Long projectId,
            boolean includeEmail);

    public List<GrantedPermissionsProjectInviteReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId in GrantedPermissionsProjectInviteService must not be null") String projectUiId,
            boolean includeEmail);

    public void cancel(User currentUser,
            @NotNull(message = "id in GrantedPermissionsProjectInviteService must not be null") Long id);
}
