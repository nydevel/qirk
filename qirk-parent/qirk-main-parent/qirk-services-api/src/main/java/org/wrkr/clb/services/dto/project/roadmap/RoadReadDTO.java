package org.wrkr.clb.services.dto.project.roadmap;

import java.util.List;

import org.wrkr.clb.model.project.roadmap.Road;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoadReadDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @JsonInclude(Include.NON_NULL)
    public Long recordVersion;

    public String name;

    @JsonInclude(Include.NON_NULL)
    public Boolean deleted;

    @JsonInclude(Include.NON_NULL)
    public List<TaskCardReadDTO> cards;

    public static RoadReadDTO fromEntity(Road road) {
        RoadReadDTO dto = new RoadReadDTO();

        dto.id = road.getId();
        dto.recordVersion = road.getRecordVersion();
        dto.name = road.getName();

        return dto;
    }

    public static RoadReadDTO fromEntityWithDeleted(Road road) {
        RoadReadDTO dto = fromEntity(road);
        dto.deleted = road.isDeleted();
        return dto;
    }
}
