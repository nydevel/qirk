package org.wrkr.clb.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RetryAfterDTO {

    @JsonProperty(value = "retry_after")
    public long retryAfterSeconds;

    public RetryAfterDTO(long retryAfterSeconds) {
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
