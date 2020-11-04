package org.wrkr.clb.services.dto.project;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnCreateBatch;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectMemberDTO extends IdDTO {
    
    public static final Long CURRENT_USER_ID = -1L;

    @JsonProperty(value = "user")
    @NotNull(message = "user in ProjectMemberDTO must not be null", groups = {
            OnCreate.class, OnCreateBatch.class })
    public Long userId;

    @JsonProperty(value = "project")
    @NotNull(message = "project in ProjectMemberDTO must not be null", groups = OnCreate.class)
    public Long projectId;

    @JsonProperty(value = "write_allowed")
    @NotNull(message = "write_allowed in ProjectMemberDTO must not be null")
    public Boolean writeAllowed;

    @NotNull(message = "manager in ProjectMemberDTO must not be null")
    public Boolean manager;

    public ProjectMemberDTO() {
    }

    public ProjectMemberDTO(Boolean writeAllowed, Boolean manager) {
        this.writeAllowed = writeAllowed;
        this.manager = manager;
    }
}
