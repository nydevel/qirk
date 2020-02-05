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
package org.wrkr.clb.services.dto.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.organization.Organization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ElasticsearchOrganizationDTO extends ElasticsearchNameAndUiIdDTO {

    private static final ObjectWriter DTO_WRITER = new ObjectMapper().writerFor(ElasticsearchOrganizationDTO.class);

    public static final String PRIVATE = "private";
    public static final String NAME_SEARCH_FIELD = "name_search_field";

    @JsonProperty(value = PRIVATE)
    public boolean isPrivate = true;
    @JsonProperty(value = NAME_SEARCH_FIELD)
    public List<String> nameSearchField;

    public static ElasticsearchOrganizationDTO fromEntity(Organization organization) {
        ElasticsearchOrganizationDTO dto = new ElasticsearchOrganizationDTO();

        dto.isPrivate = organization.isPrivate();

        dto.name = organization.getName().strip();
        dto.uiId = organization.getUiId().strip();

        dto.nameSearchField = new ArrayList<String>();
        if (!dto.name.isEmpty()) {
            dto.nameSearchField.add(dto.name);
        }
        if (!dto.uiId.isEmpty()) {
            dto.nameSearchField.add(dto.uiId);
        }

        return dto;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return DTO_WRITER.writeValueAsString(this);
    }
}
