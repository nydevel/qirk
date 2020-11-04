package org.wrkr.clb.services.dto.project;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectMemberPermissionsDTO extends IdDTO {

    @JsonProperty(value = "write_allowed")
    @NotNull(message = "write_allowed in ProjectMemberPermissionsDTO must not be null")
    public Boolean writeAllowed;

    @NotNull(message = "manager in ProjectMemberPermissionsDTO must not be null")
    public Boolean manager;
}
