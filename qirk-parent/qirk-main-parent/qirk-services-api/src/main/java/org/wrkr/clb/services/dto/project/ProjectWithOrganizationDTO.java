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

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectWithOrganizationDTO extends IdDTO {

    public String name;

    @JsonProperty(value = "ui_id")
    public String uiId;

    public static ProjectWithOrganizationDTO fromEntity(Project project) {
        ProjectWithOrganizationDTO dto = new ProjectWithOrganizationDTO();

        dto.id = project.getId();
        dto.name = project.getName();
        dto.uiId = project.getUiId();

        return dto;
    }

    public static List<ProjectWithOrganizationDTO> fromEntitiesWithOrganizationName(List<Project> projectList) {
        List<ProjectWithOrganizationDTO> dtoList = new ArrayList<ProjectWithOrganizationDTO>(projectList.size());
        for (Project project : projectList) {
            dtoList.add(fromEntity(project));
        }
        return dtoList;
    }
}
