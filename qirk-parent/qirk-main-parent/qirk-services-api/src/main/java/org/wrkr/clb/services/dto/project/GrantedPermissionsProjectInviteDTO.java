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
package org.wrkr.clb.services.dto.project;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.wrkr.clb.common.validation.groups.OnCreateByEmail;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GrantedPermissionsProjectInviteDTO extends ProjectInviteDTO {

    @NotEmpty(message = "email in GrantedPermissionsProjectInviteDTO must not be empty", groups = OnCreateByEmail.class)
    @Email(message = "email in GrantedPermissionsProjectInviteDTO must be valid", groups = OnCreateByEmail.class)
    public String email;

    @JsonProperty(value = "write_allowed")
    @NotNull(message = "write_allowed in GrantedPermissionsProjectInviteDTO must not be null")
    public Boolean writeAllowed;

    @NotNull(message = "manager in GrantedPermissionsProjectInviteDTO must not be null")
    public Boolean manager;
}
