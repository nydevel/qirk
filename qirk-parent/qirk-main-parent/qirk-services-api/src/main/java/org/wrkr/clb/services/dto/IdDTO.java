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
