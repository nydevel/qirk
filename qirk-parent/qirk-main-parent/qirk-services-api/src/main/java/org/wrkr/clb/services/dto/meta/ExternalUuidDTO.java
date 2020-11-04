package org.wrkr.clb.services.dto.meta;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalUuidDTO {

    @JsonProperty(value = "external_uuid")
    public String externalUuid;

    @Deprecated
    @JsonProperty(value = "uuid")
    public String getUuid() {
        return externalUuid;
    }

    public ExternalUuidDTO(String uuid) {
        this.externalUuid = uuid;
    }
}
