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
package org.wrkr.clb.services.dto.user;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.model.user.NotificationSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PriofileUpdateDTO {

    @JsonProperty(value = "full_name")
    @NotBlank(message = "full_name in PriofileUpdateDTO must not be blank")
    @Pattern(regexp = RegExpPattern.NO_AT_SIGN + "+", message = "full_name in PriofileUpdateDTO must not contain @")
    public String fullName;

    @NotNull(message = "about in PriofileUpdateDTO must not be null")
    public String about;

    @JsonProperty(value = "notification_settings")
    @NotNull(message = "notification_settings in PriofileUpdateDTO must not be null")
    public NotificationSettings notificationSettings = new NotificationSettings();

    @JsonProperty(value = "tags")
    @NotNull(message = "tags in PriofileUpdateDTO must not be null")
    public Set<String> tagNames = new HashSet<String>();

    @JsonProperty(value = "languages")
    @NotNull(message = "languages in PriofileUpdateDTO must not be null")
    public Set<Long> languageIds = new HashSet<Long>();
}
