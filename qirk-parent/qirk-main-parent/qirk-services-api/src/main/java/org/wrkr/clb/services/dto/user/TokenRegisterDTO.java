package org.wrkr.clb.services.dto.user;

import org.wrkr.clb.services.dto.TokenDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRegisterDTO extends TokenDTO {

    public String password;

    public String username;

    @JsonProperty(value = "full_name")
    public String fullName;
}
