package org.wrkr.clb.services.dto;

import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatPermissionsDTO {

    public String token;

    public String IV;

    @JsonProperty(value = "can_read")
    public boolean canRead;

    @JsonProperty(value = "can_write")
    public boolean canWrite;

    public ChatPermissionsDTO(TokenAndIvDTO tokenAndIV, boolean canRead, boolean canWrite) {
        this.token = tokenAndIV.token;
        this.IV = tokenAndIV.IV;
        this.canRead = canRead;
        this.canWrite = canWrite;
    }
}
