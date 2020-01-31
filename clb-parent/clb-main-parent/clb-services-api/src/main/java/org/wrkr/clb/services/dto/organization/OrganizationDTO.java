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

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganizationDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in OrganizationDTO must not be null", groups = OnUpdate.class)
    public Long recordVersion;

    @NotBlank(message = "name in OrganizationDTO must not be blank")
    public String name;

    @JsonProperty(value = "ui_id")
    @NotNull(message = "ui_id in OrganizationDTO must not be null")
    @Pattern(regexp = RegExpPattern.SLUG + "{0,23}", message = "ui_id in OrganizationDTO must be slug")
    public String uiId;

    @JsonProperty(value = "languages")
    @NotNull(message = "languages in OrganizationDTO must not be null")
    public List<Long> languageIds = new ArrayList<Long>();
}
