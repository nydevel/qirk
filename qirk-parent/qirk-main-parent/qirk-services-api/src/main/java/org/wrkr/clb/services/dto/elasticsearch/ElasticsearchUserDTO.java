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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.user.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ElasticsearchUserDTO extends ElasticsearchEntityDTO {

    private static final ObjectWriter DTO_WRITER = new ObjectMapper().writerFor(ElasticsearchUserDTO.class);

    public static final String DONT_RECOMMEND = "dont_recommend";

    public static final String USERNAME = "username";
    public static final String FULL_NAME = "full_name";
    public static final String NAME_SEARCH_FIELD = "name_search_field";

    public static final String TAGS = "tags";

    public static final String ORGANIZATIONS = "organizations";
    public static final String PROJECTS = "projects";
    public static final String INVITED_PROJECTS = "invited_projects";

    @JsonProperty(value = DONT_RECOMMEND)
    public Boolean dontRecommend;

    @JsonProperty(value = USERNAME)
    public String username;
    @JsonProperty(value = FULL_NAME)
    public String fullName;
    @JsonProperty(value = NAME_SEARCH_FIELD)
    public List<String> nameSearchField;

    @JsonProperty(value = TAGS)
    public List<String> tags;

    @JsonProperty(value = ORGANIZATIONS)
    @JsonInclude(Include.NON_NULL)
    public List<Map<String, Object>> organizations;
    @JsonProperty(value = PROJECTS)
    @JsonInclude(Include.NON_NULL)
    public List<Long> projects;
    @JsonProperty(value = INVITED_PROJECTS)
    @JsonInclude(Include.NON_NULL)
    public List<Long> invitedProjects;

    public static ElasticsearchUserDTO fromEntity(User user) {
        ElasticsearchUserDTO dto = new ElasticsearchUserDTO();

        dto.dontRecommend = user.isDontRecommend();

        dto.username = user.getUsername();
        dto.fullName = user.getFullName();
        dto.nameSearchField = Arrays.asList(dto.username, dto.fullName);

        dto.tags = new ArrayList<String>(user.getTags().size());
        for (Tag tag : user.getTags()) {
            dto.tags.add(tag.getName());
        }

        return dto;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return DTO_WRITER.writeValueAsString(this);
    }
}
