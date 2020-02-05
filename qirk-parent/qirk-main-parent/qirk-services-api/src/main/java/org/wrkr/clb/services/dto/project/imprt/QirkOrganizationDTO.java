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
package org.wrkr.clb.services.dto.project.imprt;

import java.util.List;

import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.services.dto.organization.OrganizationMemberUserDTO;
import org.wrkr.clb.services.dto.project.ProjectNameAndUiIdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QirkOrganizationDTO {

    @JsonProperty(value = "imported_projects")
    public List<ProjectNameAndUiIdDTO> importedProjects;
    public List<OrganizationMemberUserDTO> members;

    public List<TaskAttributeDTO> types;
    public List<TaskAttributeDTO> priorities;
    public List<TaskAttributeDTO> statuses;

    public static QirkOrganizationDTO fromEntities(List<Project> projectList, List<OrganizationMember> memberList) {
        QirkOrganizationDTO dto = new QirkOrganizationDTO();

        dto.importedProjects = ProjectNameAndUiIdDTO.fromEntities(projectList);
        dto.members = OrganizationMemberUserDTO.fromEntitiesWithEmail(memberList);

        dto.types = TaskAttributeDTO.fromTypes(TaskType.Type.values());
        dto.priorities = TaskAttributeDTO.fromPriorities(TaskPriority.Priority.values());
        dto.statuses = TaskAttributeDTO.fromStatuses(TaskStatus.Status.values());

        return dto;
    }
}
