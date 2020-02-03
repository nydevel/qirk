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

import org.wrkr.clb.model.user.UserFavorite;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.project.ProjectWithOrganizationDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserFavoriteReadDTO extends IdDTO {

    public ProjectWithOrganizationDTO project;

    @JsonProperty(value = "can_create_task")
    @JsonInclude(Include.NON_NULL)
    public Boolean canCreateTask;

    public static UserFavoriteReadDTO fromEntity(UserFavorite userFavorite) {
        UserFavoriteReadDTO dto = new UserFavoriteReadDTO();

        dto.id = userFavorite.getId();
        dto.project = ProjectWithOrganizationDTO.fromEntity(userFavorite.getProject());

        return dto;
    }
}
