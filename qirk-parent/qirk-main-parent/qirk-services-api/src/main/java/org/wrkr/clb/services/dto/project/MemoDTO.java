package org.wrkr.clb.services.dto.project;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MemoDTO {

    @JsonProperty(value = "project")
    @NotNull(message = "project in MemoDTO must not be null", groups = OnCreate.class)
    @Valid
    public IdOrUiIdDTO project;

    @NotNull(message = "project in MemoDTO must not be null")
    public String body;
}
