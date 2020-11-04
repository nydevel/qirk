package org.wrkr.clb.services.dto.project;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectNameAndUiIdDTO extends IdDTO {

    public String name;

    @JsonProperty(value = "ui_id")
    public String uiId;

    public static ProjectNameAndUiIdDTO fromEntity(Project project) {
        ProjectNameAndUiIdDTO dto = new ProjectNameAndUiIdDTO();

        dto.id = project.getId();
        dto.name = project.getName();
        dto.uiId = project.getUiId();

        return dto;
    }

    public static List<ProjectNameAndUiIdDTO> fromEntities(List<Project> projectList) {
        List<ProjectNameAndUiIdDTO> dtoList = new ArrayList<ProjectNameAndUiIdDTO>(projectList.size());
        for (Project project : projectList) {
            dtoList.add(fromEntity(project));
        }
        return dtoList;
    }
}
