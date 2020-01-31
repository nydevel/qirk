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
package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationSettingsDTO {

    @NotNull(message = "token in NotificationSettingsDTO must not be null")
    public String token;
    @NotNull(message = "notIVification_settings in NotificationSettingsDTO must not be null")
    public String IV;

    @JsonProperty(value = "task_created")
    @NotNull(message = "task_created in NotificationSettingsDTO must not be null")
    public Boolean taskCreated;
    @JsonProperty(value = "task_updated")
    @NotNull(message = "task_updated in NotificationSettingsDTO must not be null")
    public Boolean taskUpdated;
    @JsonProperty(value = "task_commented")
    @NotNull(message = "task_commented in NotificationSettingsDTO must not be null")
    public Boolean taskCommented;
}
