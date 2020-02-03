/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchNameAndUiIdDTO;

public class NameAndUiIdDTO extends UiIdDTO {

    public String name;

    public static NameAndUiIdDTO fromEntity(Organization organization) {
        NameAndUiIdDTO dto = new NameAndUiIdDTO();

        dto.id = organization.getId();
        dto.name = organization.getName();
        dto.uiId = organization.getUiId();

        return dto;
    }

    public static NameAndUiIdDTO fromEntity(Project project) {
        NameAndUiIdDTO dto = new NameAndUiIdDTO();

        dto.id = project.getId();
        dto.name = project.getName();
        dto.uiId = project.getUiId();

        return dto;
    }

    public static List<NameAndUiIdDTO> fromOrganizations(
            Organization predefinedOrganizaiton, List<Organization> organizationList) {
        List<NameAndUiIdDTO> dtoList = new ArrayList<NameAndUiIdDTO>(organizationList.size() + 1);
        if (predefinedOrganizaiton != null) {
            dtoList.add(fromEntity(predefinedOrganizaiton));
        }
        for (Organization organization : organizationList) {
            dtoList.add(fromEntity(organization));
        }
        return dtoList;
    }

    public static List<NameAndUiIdDTO> fromProjects(List<Project> projectList) {
        List<NameAndUiIdDTO> dtoList = new ArrayList<NameAndUiIdDTO>(projectList.size());
        for (Project project : projectList) {
            dtoList.add(fromEntity(project));
        }
        return dtoList;
    }

    public static NameAndUiIdDTO fromSearchHit(SearchHit hit) throws IOException {
        NameAndUiIdDTO dto = new NameAndUiIdDTO();
        // can't use getSourceAsMap() because it uses int for numbers
        Map<String, Object> source = JsonUtils.convertJsonToMapUsingLongForInts(hit.getSourceAsString());

        dto.id = Long.parseLong(hit.getId());
        dto.name = (String) source.get(ElasticsearchNameAndUiIdDTO.NAME);
        dto.uiId = (String) source.get(ElasticsearchNameAndUiIdDTO.UI_ID);

        return dto;
    }

    public static List<NameAndUiIdDTO> fromSearchHits(SearchHits hits) throws IOException {
        List<NameAndUiIdDTO> dtoList = new ArrayList<NameAndUiIdDTO>(hits.getHits().length);
        for (SearchHit hit : hits) {
            dtoList.add(fromSearchHit(hit));
        }
        return dtoList;
    }

    public static NameAndUiIdDTO fromMultiGetItemResponse(MultiGetItemResponse item) throws IOException {
        NameAndUiIdDTO dto = new NameAndUiIdDTO();
        // can't use getSourceAsMap() because it uses int for numbers
        Map<String, Object> source = JsonUtils.convertJsonToMapUsingLongForInts(item.getResponse().getSourceAsString());

        dto.id = Long.parseLong(item.getId());
        dto.name = (String) source.get(ElasticsearchNameAndUiIdDTO.NAME);
        dto.uiId = (String) source.get(ElasticsearchNameAndUiIdDTO.UI_ID);

        return dto;
    }
}
