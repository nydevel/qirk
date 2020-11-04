package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.NotBlank;

public class RegisterDTO extends EmailAddressDTO {

    @NotBlank(message = "password in RegisterDTO must not be blank")
    public String password;
}
