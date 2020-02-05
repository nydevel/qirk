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
package org.wrkr.clb.services.dto.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchNameAndUiIdDTO;

public class ProjectNameAndUiIdDTO extends ProjectUiIdDTO {

    public String name;

    public static ProjectNameAndUiIdDTO fromEntity(Project project) {
        ProjectNameAndUiIdDTO dto = new ProjectNameAndUiIdDTO();

        dto.id = project.getId();
        dto.name = project.getName();
        dto.uiId = project.getUiId();

        return dto;
    }

    public static List<ProjectNameAndUiIdDTO> fromEntities(List<Project> projectList) {
        List<ProjectNameAndUiIdDTO> dtoList = new ArrayList<ProjectNameAndUiIdDTO>(projectList.size());
        for (Project project : projectList) {
            dtoList.add(fromEntity(project));
        }
        return dtoList;
    }

    public static ProjectNameAndUiIdDTO fromSearchHit(SearchHit hit) throws IOException {
        ProjectNameAndUiIdDTO dto = new ProjectNameAndUiIdDTO();
        // can't use getSourceAsMap() because it uses int for numbers
        Map<String, Object> source = JsonUtils.convertJsonToMapUsingLongForInts(hit.getSourceAsString());

        dto.id = Long.parseLong(hit.getId());
        dto.name = (String) source.get(ElasticsearchNameAndUiIdDTO.NAME);
        dto.uiId = (String) source.get(ElasticsearchNameAndUiIdDTO.UI_ID);

        return dto;
    }

    public static List<ProjectNameAndUiIdDTO> fromSearchHits(SearchHits hits) throws IOException {
        List<ProjectNameAndUiIdDTO> dtoList = new ArrayList<ProjectNameAndUiIdDTO>(hits.getHits().length);
        for (SearchHit hit : hits) {
            dtoList.add(fromSearchHit(hit));
        }
        return dtoList;
    }

    public static ProjectNameAndUiIdDTO fromMultiGetItemResponse(MultiGetItemResponse item) throws IOException {
        ProjectNameAndUiIdDTO dto = new ProjectNameAndUiIdDTO();
        // can't use getSourceAsMap() because it uses int for numbers
        Map<String, Object> source = JsonUtils.convertJsonToMapUsingLongForInts(item.getResponse().getSourceAsString());

        dto.id = Long.parseLong(item.getId());
        dto.name = (String) source.get(ElasticsearchNameAndUiIdDTO.NAME);
        dto.uiId = (String) source.get(ElasticsearchNameAndUiIdDTO.UI_ID);

        return dto;
    }
}
