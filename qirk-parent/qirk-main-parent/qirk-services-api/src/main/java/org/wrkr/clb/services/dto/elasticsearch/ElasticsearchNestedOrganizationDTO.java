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

import org.wrkr.clb.model.organization.OrganizationMember;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ElasticsearchNestedOrganizationDTO {

    @JsonIgnore
    public static final String ORGANIZATION_ID = "organization_id";
    @JsonIgnore
    public static final String MEMBER_ID = "member_id";
    @JsonIgnore
    public static final String ENABLED = "enabled";

    @JsonProperty(value = MEMBER_ID)
    public Long memberId;
    @JsonProperty(value = ENABLED)
    public Boolean enabled;

    public static Map<String, Object> fromEntity(OrganizationMember member) {
        Map<String, Object> map = new HashMap<String, Object>(3);

        map.put(ORGANIZATION_ID, member.getOrganizationId());
        map.put(MEMBER_ID, member.getId());
        map.put(ENABLED, member.isEnabled());

        return map;
    }

    public static List<Map<String, Object>> fromEntities(List<OrganizationMember> memberList) {
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>(memberList.size());
        for (OrganizationMember member : memberList) {
            if (!member.isFired()) {
                mapList.add(fromEntity(member));
            }
        }
        return mapList;
    }

    public static ElasticsearchNestedOrganizationDTO fromMap(Map<String, Object> map) {
        ElasticsearchNestedOrganizationDTO dto = new ElasticsearchNestedOrganizationDTO();

        dto.memberId = (Long) map.get(MEMBER_ID);
        dto.enabled = (Boolean) map.get(ENABLED);

        return dto;
    }
}
