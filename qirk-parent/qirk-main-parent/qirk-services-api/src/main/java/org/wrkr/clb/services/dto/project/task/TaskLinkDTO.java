package org.wrkr.clb.services.dto.project.task;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskLinkDTO {

    @JsonProperty(value = "task1")
    @NotNull(message = "task1 in TaskLinkDTO must not be null")
    public Long task1Id;

    @JsonProperty(value = "task2")
    @NotNull(message = "task2 in TaskLinkDTO must not be null")
    public Long task2Id;
}
