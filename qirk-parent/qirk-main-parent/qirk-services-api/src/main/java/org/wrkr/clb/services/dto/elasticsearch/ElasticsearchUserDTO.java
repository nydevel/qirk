package org.wrkr.clb.services.dto.elasticsearch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.wrkr.clb.model.user.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ElasticsearchUserDTO extends ElasticsearchEntityDTO {

    private static final ObjectWriter DTO_WRITER = new ObjectMapper().writerFor(ElasticsearchUserDTO.class);

    public static final String USERNAME = "username";
    public static final String FULL_NAME = "full_name";
    public static final String NAME_SEARCH_FIELD = "name_search_field";

    public static final String PROJECTS = "projects";
    public static final String INVITED_PROJECTS = "invited_projects";

    @JsonProperty(value = USERNAME)
    public String username;
    @JsonProperty(value = FULL_NAME)
    public String fullName;
    @JsonProperty(value = NAME_SEARCH_FIELD)
    public List<String> nameSearchField;

    @JsonProperty(value = PROJECTS)
    @JsonInclude(Include.NON_NULL)
    public List<Map<String, Object>> projects;
    @JsonProperty(value = INVITED_PROJECTS)
    @JsonInclude(Include.NON_NULL)
    public List<Long> invitedProjects;

    public static ElasticsearchUserDTO fromEntity(User user) {
        ElasticsearchUserDTO dto = new ElasticsearchUserDTO();

        dto.username = user.getUsername();
        dto.fullName = user.getFullName();
        dto.nameSearchField = Arrays.asList(dto.username, dto.fullName);

        return dto;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return DTO_WRITER.writeValueAsString(this);
    }
}
