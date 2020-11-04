package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.services.dto.TokenDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivationDTO extends TokenDTO {

    @NotNull(message = "username in ActivationDTO must not be null")
    @Pattern(regexp = RegExpPattern.LOWER_CASE_SLUG + "{1,25}", message = "username in ActivationDTO must be lower case slug")
    public String username;

    @JsonProperty(value = "full_name")
    @NotBlank(message = "full_name in ActivationDTO must not be blank")
    public String fullName;
}
