package org.wrkr.clb.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NameCodeDTO {

    @JsonProperty(value = "name_code")
    public String nameCode;

    public NameCodeDTO(String nameCode) {
        this.nameCode = nameCode;
    }
}
