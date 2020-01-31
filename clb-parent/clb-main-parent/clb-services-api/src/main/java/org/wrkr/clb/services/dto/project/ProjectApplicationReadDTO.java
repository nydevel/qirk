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

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.services.dto.user.PublicUserDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectApplicationReadDTO extends ProjectApplicationStatusDTO {

    @JsonInclude(Include.NON_NULL)
    public PublicUserDTO user;

    @JsonInclude(Include.NON_NULL)
    public ProjectWithOrganizationDTO project;

    public String text;

    @JsonProperty(value = "created_at")
    public String createdAt;

    @JsonProperty(value = "updated_at")
    public String updatedAt;

    public static ProjectApplicationReadDTO fromEntity(ProjectApplication application,
            boolean includeUser, boolean includeProject, boolean includeStatus) {
        ProjectApplicationReadDTO dto = new ProjectApplicationReadDTO();

        dto.id = application.getId();
        dto.text = application.getText();
        dto.createdAt = application.getCreatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        dto.updatedAt = application.getUpdatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);

        if (includeUser) {
            dto.user = PublicUserDTO.fromEntity(application.getUser());
        }
        if (includeProject) {
            dto.project = ProjectWithOrganizationDTO.fromEntityWithOrganizationName(application.getProject());
        }
        if (includeStatus) {
            dto.status = application.getStatus();
        }

        return dto;
    }

    public static ProjectApplicationReadDTO fromEntity(ProjectApplication application) {
        return fromEntity(application, false, false, false);
    }

    public static List<ProjectApplicationReadDTO> fromEntities(List<ProjectApplication> applicationList,
            boolean includeUser, boolean includeProject, boolean includeStatus) {
        List<ProjectApplicationReadDTO> dtoList = new ArrayList<ProjectApplicationReadDTO>(applicationList.size());
        for (ProjectApplication application : applicationList) {
            dtoList.add(fromEntity(application, includeUser, includeProject, includeStatus));
        }
        return dtoList;
    }

    public static List<ProjectApplicationReadDTO> fromEntitiesForUser(List<ProjectApplication> applicationList) {
        return fromEntities(applicationList, false, true, true);
    }

    public static List<ProjectApplicationReadDTO> fromEntitiesForProject(List<ProjectApplication> applicationList) {
        return fromEntities(applicationList, true, false, false);
    }
}
