/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
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
