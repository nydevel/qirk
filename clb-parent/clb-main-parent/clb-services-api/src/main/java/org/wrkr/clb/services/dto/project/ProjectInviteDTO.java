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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnCreateByEmail;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectInviteDTO extends IdDTO {

    @JsonProperty(value = "user")
    @NotNull(message = "user in ProjectInviteDTO must not be null", groups = OnCreate.class)
    public Long userId;

    @JsonProperty(value = "project")
    @NotNull(message = "project in ProjectInviteDTO must not be null", groups = { OnCreate.class, OnCreateByEmail.class })
    public Long projectId;

    @NotNull(message = "text in ProjectInviteDTO must not be null", groups = OnCreate.class)
    @Null(message = "text in ProjectInviteDTO must be null", groups = OnCreateByEmail.class)
    public String text;
}
