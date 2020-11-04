package org.wrkr.clb.services.dto;

import javax.validation.constraints.NotNull;

public class OAuthCodeDTO extends VersionedEntityDTO {

    @NotNull(message = "code in OAuthCodeDTO must not be null")
    public String code;
}
