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
package org.wrkr.clb.services.dto.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchUserDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicUserDTO extends IdDTO {

    public String username;

    @JsonProperty(value = "full_name")
    public String fullName;

    public static PublicUserDTO fromEntity(User user) {
        PublicUserDTO dto = new PublicUserDTO();

        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.fullName = user.getFullName();

        return dto;
    }

    public static List<PublicUserDTO> fromEntities(List<User> userList) {
        List<PublicUserDTO> dtoList = new ArrayList<PublicUserDTO>(userList.size());
        for (User user : userList) {
            PublicUserDTO dto = PublicUserDTO.fromEntity(user);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public static PublicUserDTO fromIdAndSourceMap(Long id, Map<String, Object> source) {
        PublicUserDTO dto = new PublicUserDTO();

        dto.id = id;
        dto.username = (String) source.get(ElasticsearchUserDTO.USERNAME);
        dto.fullName = (String) source.get(ElasticsearchUserDTO.FULL_NAME);

        return dto;
    }

    public static PublicUserDTO fromSearchHit(SearchHit hit) throws IOException {
        // can't use getSourceAsMap() because it uses int for numbers
        Map<String, Object> source = JsonUtils.convertJsonToMapUsingLongForInts(hit.getSourceAsString());
        return fromIdAndSourceMap(Long.parseLong(hit.getId()), source);
    }

    public static List<PublicUserDTO> fromSearchHits(SearchHits hits) throws IOException {
        List<PublicUserDTO> dtoList = new ArrayList<PublicUserDTO>(hits.getHits().length);
        for (SearchHit hit : hits) {
            dtoList.add(fromSearchHit(hit));
        }
        return dtoList;
    }
}