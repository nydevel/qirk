package org.wrkr.clb.chat.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalUuidDTO {

    @JsonProperty(value = "external_uuid")
    public String externalUuid;
    
    public ExternalUuidDTO(String externalUuid) {
        this.externalUuid = externalUuid;
    }
}
