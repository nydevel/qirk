package org.wrkr.clb.services.dto.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserIdsDTO {

    @JsonProperty(value = "users")
    public List<Long> userIds;

    public UserIdsDTO(List<Long> userIds) {
        this.userIds = userIds;
    }
}
