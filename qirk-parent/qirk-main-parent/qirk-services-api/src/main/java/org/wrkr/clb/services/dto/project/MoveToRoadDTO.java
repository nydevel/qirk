package org.wrkr.clb.services.dto.project;

import org.wrkr.clb.services.dto.MoveDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MoveToRoadDTO extends MoveDTO {

    @JsonProperty(value = "road")
    public Long roadId;
}
