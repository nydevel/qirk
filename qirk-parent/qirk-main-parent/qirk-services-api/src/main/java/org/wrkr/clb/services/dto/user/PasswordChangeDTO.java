package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordChangeDTO {

    public String password;

    @JsonProperty(value = "new_password")
    @NotBlank(message = "new_password in PasswordChangeDTO must not be blank")
    public String newPassword;

    public String token;
}
