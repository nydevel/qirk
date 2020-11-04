package org.wrkr.clb.services.dto.project;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.Project;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectInviteOptionDTO extends ProjectNameAndUiIdDTO {

    @JsonProperty(value = "is_member")
    public boolean isMember = false;

    @JsonProperty(value = "invite")
    public ProjectInviteStatusDTO invite;

    public static ProjectInviteOptionDTO fromEntity(Project project) {
        ProjectInviteOptionDTO dto = new ProjectInviteOptionDTO();

        dto.id = project.getId();
        dto.name = project.getName();
        dto.uiId = project.getUiId();

        return dto;
    }

    public static List<ProjectInviteOptionDTO> fromTuples(List<Project> projectList) {
        List<ProjectInviteOptionDTO> dtoList = new ArrayList<ProjectInviteOptionDTO>();
        for (Project project : projectList) {
            dtoList.add(fromEntity(project));
        }
        return dtoList;
    }
}
