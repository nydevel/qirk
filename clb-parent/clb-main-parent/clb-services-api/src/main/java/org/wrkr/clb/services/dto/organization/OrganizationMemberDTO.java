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
package org.wrkr.clb.services.dto.organization;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganizationMemberDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in OrganizationMemberDTO must not be null", groups = OnUpdate.class)
    public Long recordVersion;

    @JsonProperty(value = "user")
    @NotNull(message = "user in OrganizationMemberDTO must not be null", groups = OnCreate.class)
    public Long userId;

    @JsonProperty(value = "organization")
    @NotNull(message = "organization in OrganizationMemberDTO must not be null", groups = OnCreate.class)
    @Valid
    public IdOrUiIdDTO organization;

    @NotNull(message = "enabled in OrganizationMemberDTO must not be null")
    public Boolean enabled = true;

    @NotNull(message = "manager in OrganizationMemberDTO must not be null")
    public Boolean manager = false;
}
