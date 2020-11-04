package org.wrkr.clb.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MoveDTO extends IdDTO {

    @JsonProperty(value = "old_previous")
    public Long oldPreviousId;

    @JsonProperty(value = "old_next")
    public Long oldNextId;

    @JsonProperty(value = "previous")
    public Long previousId;

    @JsonProperty(value = "next")
    public Long nextId;
}
