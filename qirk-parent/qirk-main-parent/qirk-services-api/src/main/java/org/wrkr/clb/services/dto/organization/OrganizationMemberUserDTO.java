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
package org.wrkr.clb.services.dto.organization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchNestedOrganizationDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchUserDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;
import org.wrkr.clb.services.dto.user.PublicUserWithEmailDTO;

public class OrganizationMemberUserDTO extends IdDTO {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationMemberUserDTO.class);

    public Boolean enabled;

    public PublicUserDTO user;

    public static OrganizationMemberUserDTO fromEntity(OrganizationMember member) {
        OrganizationMemberUserDTO dto = new OrganizationMemberUserDTO();

        dto.id = member.getId();
        dto.enabled = member.isEnabled() && !member.isFired();
        dto.user = PublicUserDTO.fromEntity(member.getUser());

        return dto;
    }

    public static OrganizationMemberUserDTO fromEntityWithEmail(OrganizationMember member) {
        OrganizationMemberUserDTO dto = new OrganizationMemberUserDTO();

        dto.id = member.getId();
        dto.enabled = member.isEnabled() && !member.isFired();
        dto.user = PublicUserWithEmailDTO.fromEntity(member.getUser());

        return dto;
    }

    public static List<OrganizationMemberUserDTO> fromEntities(Collection<OrganizationMember> memberList) {
        List<OrganizationMemberUserDTO> dtoList = new ArrayList<OrganizationMemberUserDTO>(memberList.size());
        for (OrganizationMember member : memberList) {
            dtoList.add(fromEntity(member));
        }
        return dtoList;
    }

    public static List<OrganizationMemberUserDTO> fromEntitiesWithEmail(Collection<OrganizationMember> memberList) {
        List<OrganizationMemberUserDTO> dtoList = new ArrayList<OrganizationMemberUserDTO>(memberList.size());
        for (OrganizationMember member : memberList) {
            dtoList.add(fromEntityWithEmail(member));
        }
        return dtoList;
    }

    public static OrganizationMemberUserDTO fromSearchHit(SearchHit hit, Long organizationId) throws IOException {
        OrganizationMemberUserDTO dto = new OrganizationMemberUserDTO();

        for (SearchHit memberHit : hit.getInnerHits().get(ElasticsearchUserDTO.ORGANIZATIONS)) {
            Map<String, Object> memberSource = JsonUtils.convertJsonToMapUsingLongForInts(memberHit.getSourceAsString());
            if (organizationId.equals((Long) memberSource.get(ElasticsearchNestedOrganizationDTO.ORGANIZATION_ID))) {
                dto.id = (Long) memberSource.get(ElasticsearchNestedOrganizationDTO.MEMBER_ID);
                dto.enabled = (Boolean) memberSource.get(ElasticsearchNestedOrganizationDTO.ENABLED);
                break;
            }
        }
        dto.user = PublicUserDTO.fromSearchHit(hit);

        return dto;
    }

    public static List<OrganizationMemberUserDTO> fromSearchHits(SearchHits hits, Long organizationId, Long firstUserId)
            throws IOException {
        List<OrganizationMemberUserDTO> dtoList = new ArrayList<OrganizationMemberUserDTO>(hits.getHits().length);
        for (SearchHit hit : hits) {
            OrganizationMemberUserDTO dto = fromSearchHit(hit, organizationId);
            if (dto.user.id.equals(firstUserId)) {
                dtoList.add(0, dto);
            } else {
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
}
