package org.wrkr.clb.services.dto;

import javax.validation.constraints.NotNull;

public class TokenDTO {

    @NotNull(message = "token in TokenDTO must not be null")
    public String token;

    public TokenDTO() {
    }

    public TokenDTO(String token) {
        this.token = token;
    }
}
