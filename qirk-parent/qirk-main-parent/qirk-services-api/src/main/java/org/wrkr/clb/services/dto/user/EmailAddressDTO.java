package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailAddressDTO {

    @JsonProperty(value = "email")
    @NotEmpty(message = "email in EmailDTO must not be empty")
    @Email(message = "email in EmailDTO must be valid")
    public String emailAddress;

    public static EmailAddressDTO fromEmail(String emailAddress) {
        EmailAddressDTO dto = new EmailAddressDTO();
        dto.emailAddress = emailAddress;
        return dto;
    }
}
