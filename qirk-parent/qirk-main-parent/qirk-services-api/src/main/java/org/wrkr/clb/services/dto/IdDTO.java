package org.wrkr.clb.services.dto;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.BaseIdEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class IdDTO extends BaseEntityDTO {

    @JsonInclude(Include.NON_NULL)
    @NotNull(message = "id in IdDTO must not be null", groups = OnUpdate.class)
    public Long id;

    public IdDTO() {
    }

    public IdDTO(Long id) {
        this.id = id;
    }

    public static IdDTO fromEntity(BaseIdEntity entity) {
        IdDTO dto = new IdDTO();
        if (entity != null) {
            dto.id = entity.getId();
        }
        return dto;
    }
}
