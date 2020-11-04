package org.wrkr.clb.services.dto;

import org.wrkr.clb.common.validation.IdAndUiIdObject;
import org.wrkr.clb.common.validation.constraints.NotNullIdOrUiId;

import com.fasterxml.jackson.annotation.JsonProperty;


@NotNullIdOrUiId(message = "Exactly one of fields 'id' and 'ui_id' in UiIdDTO must not be null")
public class IdOrUiIdDTO extends BaseEntityDTO implements IdAndUiIdObject {

    public Long id; // don't extend it from IdDTO, since validation usage is different

    @JsonProperty(value = "ui_id")
    public String uiId;

    public IdOrUiIdDTO() {
    }

    public IdOrUiIdDTO(Long id) {
        this.id = id;
    }

    public IdOrUiIdDTO(String uiId) {
        this.uiId = (uiId == null ? null : uiId.strip());
    }

    public IdOrUiIdDTO(Long id, String uiId) {
        this.id = id;
        this.uiId = (uiId == null ? null : uiId.strip());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getUiId() {
        return uiId;
    }
}
