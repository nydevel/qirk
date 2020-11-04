package org.wrkr.clb.notification.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LastCheckDTO {

    @JsonProperty(value = "last_check")
    public Long lastCheck;

    public LastCheckDTO(Long lastCheck) {
        this.lastCheck = lastCheck;
    }
}
