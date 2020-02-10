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

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectReadDTO extends ProjectNameAndUiIdDTO {

    @JsonProperty(value = "record_version")
    public Long recordVersion;

    @JsonInclude(Include.NON_NULL)
    public String key;

    @JsonProperty(value = "private")
    public Boolean isPrivate;

    @Deprecated
    @JsonProperty(value = "can_be_public")
    public Boolean canBePublic = true;

    @JsonProperty(value = "description_html")
    @JsonInclude(Include.NON_NULL)
    public String descriptionHtml;

    @JsonProperty(value = "description_md")
    @JsonInclude(Include.NON_NULL)
    public String descriptionMd;

    @JsonProperty(value = "documentation_html")
    @JsonInclude(Include.NON_NULL)
    public String documentationHtml;

    @JsonProperty(value = "documentation_md")
    @JsonInclude(Include.NON_NULL)
    public String documentationMd;

    @JsonInclude(Include.NON_NULL)
    public List<Tag> tags;

    @JsonInclude(Include.NON_NULL)
    public List<Language> languages;

    @JsonProperty(value = "is_member")
    @JsonInclude(Include.NON_NULL)
    public boolean isMember = false;

    @JsonProperty(value = "write_allowed")
    @JsonInclude(Include.NON_NULL)
    public boolean writeAllowed = false;

    @JsonProperty(value = "can_manage")
    @JsonInclude(Include.NON_NULL)
    public boolean canManage = false;

    @JsonProperty(value = "application")
    @JsonInclude(Include.NON_NULL)
    public ProjectApplicationStatusDTO application;

    private static ProjectReadDTO fromEntity(Project project,
            boolean includeDescription, boolean includeDoc, boolean includeTagsAndLanguages) {
        ProjectReadDTO dto = new ProjectReadDTO();

        dto.id = project.getId();
        dto.recordVersion = project.getRecordVersion();
        dto.name = project.getName();
        dto.uiId = project.getUiId();
        dto.key = project.getKey();
        dto.isPrivate = project.isPrivate();

        if (includeDescription) {
            dto.descriptionHtml = project.getDescriptionHtml();
            dto.descriptionMd = project.getDescriptionMd();
        }

        if (includeDoc) {
            dto.documentationHtml = project.getDocumentationHtml();
            dto.documentationMd = project.getDocumentationMd();
        }

        if (includeTagsAndLanguages) {
            dto.tags = project.getTags();
            dto.languages = project.getLanguages();
        }

        return dto;
    }

    public static ProjectReadDTO fromEntityWithDescription(Project project) {
        return fromEntity(project, true, false, false);
    }

    public static ProjectReadDTO fromEntityWithDescriptionAndDocs(Project project) {
        return fromEntity(project, true, true, false);
    }

    public static ProjectReadDTO fromEntityWithEverythingForRead(Project project) {
        ProjectReadDTO dto = fromEntity(project, true, false, true);

        ProjectMember currentProjectMember = (project.getMembers().isEmpty()
                ? null
                : project.getMembers().get(0));

        dto.isMember = (currentProjectMember != null);
        dto.canManage = (currentProjectMember != null && currentProjectMember.isManager());
        dto.writeAllowed = (dto.canManage || (currentProjectMember != null && currentProjectMember.isWriteAllowed()));

        return dto;
    }

    public static List<ProjectReadDTO> fromEntitiesWithEverythingForList(List<Project> projectList) {
        List<ProjectReadDTO> dtoList = new ArrayList<ProjectReadDTO>(projectList.size());
        for (Project project : projectList) {
            dtoList.add(fromEntity(project, false, false, false));
        }
        return dtoList;
    }
}
