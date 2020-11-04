package org.wrkr.clb.services.dto.project.roadmap;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskCardDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in RoadDTO must not be null", groups = OnUpdate.class)
    public Long recordVersion;

    @JsonProperty(value = "road")
    @NotNull(message = "road_id in TaskCardDTO must not be null", groups = OnCreate.class)
    public Long roadId;

    @NotBlank(message = "name in TaskCardDTO must not be blank")
    public String name;

    @JsonProperty(value = "status")
    @NotNull(message = "status in TaskCardDTO must not be null")
    public String statusNameCode = TaskCard.Status.STOPPED.toString();
}
