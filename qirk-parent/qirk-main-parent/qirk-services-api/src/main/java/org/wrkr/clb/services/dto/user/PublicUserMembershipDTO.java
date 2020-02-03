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
package org.wrkr.clb.services.dto.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchNestedOrganizationDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchUserDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicUserMembershipDTO extends PublicUserDTO {

    @SuppressWarnings("unused")
    private final static Logger LOG = LoggerFactory.getLogger(PublicUserMembershipDTO.class);

    @JsonProperty(value = "is_member")
    public boolean isMember = false;

    @JsonProperty(value = "organization_member")
    @JsonInclude(Include.NON_NULL)
    public ElasticsearchNestedOrganizationDTO organizationMember;

    public static PublicUserMembershipDTO fromSearchHit(SearchHit hit, Long organizationId) throws IOException {
        PublicUserMembershipDTO dto = new PublicUserMembershipDTO();
        // can't use getSourceAsMap() because it uses int for numbers
        Map<String, Object> userSource = JsonUtils.convertJsonToMapUsingLongForInts(hit.getSourceAsString());

        dto.id = Long.parseLong(hit.getId());
        dto.username = (String) userSource.get(ElasticsearchUserDTO.USERNAME);
        dto.fullName = (String) userSource.get(ElasticsearchUserDTO.FULL_NAME);
        dto.dontRecommend = (Boolean) userSource.get(ElasticsearchUserDTO.DONT_RECOMMEND);

        if (organizationId != null) {
            for (SearchHit memberHit : hit.getInnerHits().get(ElasticsearchUserDTO.ORGANIZATIONS)) {
                Map<String, Object> memberSource = JsonUtils.convertJsonToMapUsingLongForInts(memberHit.getSourceAsString());
                if (((Long) memberSource.get(ElasticsearchNestedOrganizationDTO.ORGANIZATION_ID)).equals(organizationId)) {
                    dto.isMember = true;
                    dto.organizationMember = ElasticsearchNestedOrganizationDTO.fromMap(memberSource);
                    break;
                }
            }
        }

        return dto;
    }

    public static List<PublicUserMembershipDTO> fromSearchHits(
            SearchHits memberHits, SearchHits nonMemberHits, Long organizationId) throws IOException {
        List<PublicUserMembershipDTO> dtoList = new ArrayList<PublicUserMembershipDTO>(
                memberHits.getHits().length + nonMemberHits.getHits().length);
        for (SearchHit hit : memberHits) {
            dtoList.add(fromSearchHit(hit, organizationId));
        }
        for (SearchHit hit : nonMemberHits) {
            dtoList.add(fromSearchHit(hit, null));
        }
        return dtoList;
    }
}