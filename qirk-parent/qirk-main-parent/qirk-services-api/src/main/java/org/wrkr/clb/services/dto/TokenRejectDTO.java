package org.wrkr.clb.services.dto;

import javax.validation.constraints.NotNull;

public class TokenRejectDTO extends TokenDTO {

    @NotNull(message = "reported in TokenRejectDTO must not be null")
    public Boolean reported;
}
