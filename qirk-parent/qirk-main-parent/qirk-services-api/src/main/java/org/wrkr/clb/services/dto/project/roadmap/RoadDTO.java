package org.wrkr.clb.services.dto.project.roadmap;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoadDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in RoadDTO must not be null", groups = OnUpdate.class)
    public Long recordVersion;

    @JsonProperty(value = "project")
    @NotNull(message = "project_id in RoadDTO must not be null", groups = OnCreate.class)
    public Long projectId;

    @NotBlank(message = "name in RoadDTO must not be blank")
    public String name;
}
