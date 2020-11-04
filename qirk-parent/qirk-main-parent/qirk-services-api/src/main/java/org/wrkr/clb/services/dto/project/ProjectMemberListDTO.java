package org.wrkr.clb.services.dto.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectMemberListDTO {

    @NotEmpty(message = "members in ProjectMemberListDTO must not be empty")
    @Valid
    public List<ProjectMemberDTO> members;

    @JsonProperty(value = "project")
    @NotNull(message = "project in ProjectMemberListDTO must not be null")
    public Long projectId;
}
