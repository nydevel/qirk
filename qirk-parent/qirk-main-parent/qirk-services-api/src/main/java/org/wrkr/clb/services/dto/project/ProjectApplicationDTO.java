package org.wrkr.clb.services.dto.project;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Evgeny Poreykin
 *
 */
public class ProjectApplicationDTO {

    @JsonProperty(value = "project")
    @NotNull(message = "project in ProjectApplicationDTO must not be null")
    public Long projectId;

    @NotNull(message = "text in ProjectApplicationDTO must not be null")
    public String text;
}
