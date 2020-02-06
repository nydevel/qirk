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
package org.wrkr.clb.services.dto.project;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in ProjectDTO must not be null", groups = OnUpdate.class)
    public Long recordVersion;

    @NotBlank(message = "name in ProjectDTO must not be blank")
    public String name;

    @JsonProperty(value = "ui_id")
    @NotNull(message = "ui_id in ProjectDTO must not be null")
    @Pattern(regexp = RegExpPattern.SLUG + "{0,23}", message = "ui_id in ProjectDTO must be slug")
    public String uiId;

    @NotNull(message = "key in ProjectDTO must not be null")
    public String key = "";

    // @JsonProperty(value = "private") TODO turn on
    @JsonIgnore
    @NotNull(message = "private in ProjectDTO must not be null")
    public Boolean isPrivate = true;

    @NotNull(message = "description in ProjectDTO must not be null")
    @Size(max = 10000, message = "description in ProjectDTO must not be no more than 10000 characters")
    public String description;

    @JsonProperty(value = "make_me_member")
    public boolean makeMeMember = false;

    @JsonProperty(value = "tags")
    @NotNull(message = "tags in ProjectDTO must not be null")
    public Set<String> tagNames = new HashSet<String>();

    @JsonProperty(value = "languages")
    @NotNull(message = "languages in ProjectDTO must not be null")
    public Set<Long> languageIds = new HashSet<Long>();
}
