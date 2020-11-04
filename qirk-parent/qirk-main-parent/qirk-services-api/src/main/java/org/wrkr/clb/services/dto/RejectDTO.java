package org.wrkr.clb.services.dto;

import javax.validation.constraints.NotNull;

public class RejectDTO extends IdDTO {

    @NotNull(message = "reported in RejectDTO must not be null")
    public Boolean reported;
}
