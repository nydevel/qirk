package org.wrkr.clb.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpiresAtDTO {

    @JsonProperty(value = "expires_at")
    public long expiresAt;

    public ExpiresAtDTO(long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
