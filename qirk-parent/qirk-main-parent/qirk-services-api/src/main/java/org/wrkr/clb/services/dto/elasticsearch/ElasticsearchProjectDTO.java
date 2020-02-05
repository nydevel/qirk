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

import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.project.Project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ElasticsearchProjectDTO extends ElasticsearchNameAndUiIdDTO {

    private static final ObjectWriter DTO_WRITER = new ObjectMapper().writerFor(ElasticsearchProjectDTO.class);

    public static final String PRIVATE = "private";

    public static final String NAME_SEARCH_FIELD = "name_search_field";
    public static final String DESCRIPTION = "description";
    public static final String TAGS = "tags";

    public static final String ORGANIZATION_ID = "organization_id";

    @JsonProperty(value = PRIVATE)
    public boolean isPrivate = true;

    @JsonProperty(value = NAME_SEARCH_FIELD)
    public List<String> nameSearchField;
    @JsonProperty(value = DESCRIPTION)
    public String description;
    @JsonProperty(value = TAGS)
    public List<String> tags;

    @JsonProperty(value = ORGANIZATION_ID)
    public long organizationId;

    public static ElasticsearchProjectDTO fromEntity(Project project) {
        ElasticsearchProjectDTO dto = new ElasticsearchProjectDTO();

        dto.isPrivate = project.isPrivate();

        dto.name = project.getName().strip();
        dto.uiId = project.getUiId().strip();

        dto.nameSearchField = new ArrayList<String>();
        if (!dto.name.isEmpty()) {
            dto.nameSearchField.add(dto.name);
        }
        if (!dto.uiId.isEmpty()) {
            dto.nameSearchField.add(dto.uiId);
        }

        dto.description = project.getDescriptionMd().strip();
        List<Tag> tags = project.getTags();
        dto.tags = new ArrayList<String>(tags.size());
        for (Tag tag : tags) {
            dto.tags.add(tag.getName());
        }

        dto.organizationId = project.getOrganization().getId();

        return dto;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return DTO_WRITER.writeValueAsString(this);
    }
}
