package org.wrkr.clb.services.dto.project.task;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttachmentCreateDTO {

    @JsonProperty(value = "task")
    @NotNull(message = "task in AttachmentCreateDTO must not be null")
    public Long taskId;

    @NotNull(message = "uuids in AttachmentCreateDTO must not be null")
    @NotEmpty(message = "uuids in AttachmentCreateDTO must not be empty")
    public List<String> uuids;
}
