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
package org.wrkr.clb.services.dto;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.model.BaseVersionedEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordVersionDTO {

    @NotNull(message = "id in RecordVersionDTO must not be null")
    public Long id; // don't extend it from IdDTO, since validation usage is different

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in RecordVersionDTO must not be null")
    public Long recordVersion;

    public static RecordVersionDTO fromEntity(BaseVersionedEntity entity) {
        RecordVersionDTO dto = new RecordVersionDTO();

        dto.id = entity.getId();
        dto.recordVersion = entity.getRecordVersion();

        return dto;
    }
}
