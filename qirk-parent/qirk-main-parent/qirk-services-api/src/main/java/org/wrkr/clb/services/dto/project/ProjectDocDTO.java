package org.wrkr.clb.services.dto.project;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.services.dto.VersionedEntityDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectDocDTO extends VersionedEntityDTO {

    @JsonInclude(Include.NON_NULL)
    @NotNull(message = "documentation in DocumentationDTO must not be null")
    public String documentation;

    @JsonProperty(value = "documentation_html")
    @Null(message = "documentation_html in DocumentationDTO must be null")
    public String documentationHtml;

    @JsonProperty(value = "documentation_md")
    @Null(message = "documentation_md in DocumentationDTO must be null")
    public String documentationMd;

    public static ProjectDocDTO fromEntity(Project project) {
        ProjectDocDTO dto = new ProjectDocDTO();

        dto.id = project.getId();
        dto.recordVersion = project.getRecordVersion();
        dto.documentationHtml = project.getDocumentationHtml();
        dto.documentationMd = project.getDocumentationMd();

        return dto;
    }
}
