package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginDTO {

    @JsonProperty(value = "username")
    @NotNull(message = "username in LoginDTO must not be null")
    public String usernameOrEmailAdress;

    @NotNull(message = "password in LoginDTO must not be null")
    public String password;
}
