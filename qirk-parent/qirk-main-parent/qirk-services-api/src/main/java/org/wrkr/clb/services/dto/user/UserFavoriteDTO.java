package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserFavoriteDTO {

    @JsonProperty(value = "project")
    @NotNull(message = "project in UserFavoriteDTO must not be null")
    public Long projectId;
}
