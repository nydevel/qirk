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
package org.wrkr.clb.services.dto.project.task;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.NameCodeDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchTaskDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchedTaskDTO extends IdDTO {

    public Long number;

    public String summary;

    @JsonProperty(value = "created_at")
    public String createdAt;

    @JsonProperty(value = "updated_at")
    public String updatedAt;

    public IdDTO reporter;

    public IdDTO assignee;

    @JsonProperty(value = "task_type")
    public NameCodeDTO taskType;

    @JsonProperty(value = "task_priority")
    public NameCodeDTO taskPriority;

    @JsonProperty(value = "task_status")
    public NameCodeDTO taskStatus;

    public static SearchedTaskDTO fromSearchHit(SearchHit hit) throws IOException {
        SearchedTaskDTO dto = new SearchedTaskDTO();
        // can't use getSourceAsMap() because it uses int for numbers
        Map<String, Object> source = JsonUtils.convertJsonToMapUsingLongForInts(hit.getSourceAsString());

        dto.id = Long.parseLong(hit.getId());
        dto.number = (Long) source.get(ElasticsearchTaskDTO.NUMBER);
        dto.summary = (String) source.get(ElasticsearchTaskDTO.SUMMARY);

        dto.reporter = new IdDTO((Long) source.get(ElasticsearchTaskDTO.REPORTER));
        Long assigneeId = (Long) source.get(ElasticsearchTaskDTO.ASSIGNEE);
        if (assigneeId != null) {
            dto.assignee = new IdDTO(assigneeId);
        }

        Long createdAtTimestamp = (Long) source.get(ElasticsearchTaskDTO.CREATED_AT);
        if (createdAtTimestamp != null) {
            dto.createdAt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(createdAtTimestamp), DateTimeUtils.DEFAULT_TIME_ZONE_ID)
                    .format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        }
        Long updatedAtTimestamp = (Long) source.get(ElasticsearchTaskDTO.UPDATED_AT);
        if (updatedAtTimestamp != null) {
            dto.updatedAt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(updatedAtTimestamp), DateTimeUtils.DEFAULT_TIME_ZONE_ID)
                    .format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        }

        dto.taskType = new NameCodeDTO((String) source.get(ElasticsearchTaskDTO.TASK_TYPE));
        dto.taskPriority = new NameCodeDTO((String) source.get(ElasticsearchTaskDTO.TASK_PRIORITY));
        dto.taskStatus = new NameCodeDTO((String) source.get(ElasticsearchTaskDTO.TASK_STATUS));

        return dto;
    }

    public static List<SearchedTaskDTO> fromHits(SearchHits hits) throws IOException {
        List<SearchedTaskDTO> dtoList = new ArrayList<SearchedTaskDTO>(hits.getHits().length);
        for (SearchHit hit : hits) {
            dtoList.add(fromSearchHit(hit));
        }
        return dtoList;
    }
}
