package org.wrkr.clb.services.dto;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.model.VersionedIdEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VersionedEntityDTO {

    @NotNull(message = "id in RecordVersionDTO must not be null")
    public Long id; // don't extend it from IdDTO, since validation usage is different

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in RecordVersionDTO must not be null")
    public Long recordVersion;

    public static VersionedEntityDTO fromEntity(VersionedIdEntity entity) {
        VersionedEntityDTO dto = new VersionedEntityDTO();

        dto.id = entity.getId();
        dto.recordVersion = entity.getRecordVersion();

        return dto;
    }
}
