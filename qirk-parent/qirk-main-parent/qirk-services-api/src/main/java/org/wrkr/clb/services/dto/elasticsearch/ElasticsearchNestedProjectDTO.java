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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wrkr.clb.model.project.ProjectMember;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Evgeny Poreykin
 *
 */
public class ElasticsearchNestedProjectDTO {

    @JsonIgnore
    public static final String PROJECT_ID = "project_id";
    @JsonIgnore
    public static final String MEMBER_ID = "member_id";

    @JsonProperty(value = MEMBER_ID)
    public Long memberId;

    public static Map<String, Object> fromEntity(ProjectMember member) {
        Map<String, Object> map = new HashMap<String, Object>(3);

        map.put(PROJECT_ID, member.getProjectId());
        map.put(MEMBER_ID, member.getId());

        return map;
    }

    public static List<Map<String, Object>> fromEntities(List<ProjectMember> memberList) {
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>(memberList.size());
        for (ProjectMember member : memberList) {
            if (!member.isFired()) {
                mapList.add(fromEntity(member));
            }
        }
        return mapList;
    }

    public static ElasticsearchNestedProjectDTO fromMap(Map<String, Object> map) {
        ElasticsearchNestedProjectDTO dto = new ElasticsearchNestedProjectDTO();
        dto.memberId = (Long) map.get(MEMBER_ID);
        return dto;
    }
}
