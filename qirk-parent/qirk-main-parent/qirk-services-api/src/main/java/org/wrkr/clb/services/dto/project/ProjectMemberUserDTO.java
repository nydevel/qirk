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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchNestedProjectDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchUserDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;
import org.wrkr.clb.services.dto.user.PublicUserWithEmailDTO;

public class ProjectMemberUserDTO extends IdDTO {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ProjectMemberUserDTO.class);

    public PublicUserDTO user;

    public static ProjectMemberUserDTO fromEntity(ProjectMember member) {
        ProjectMemberUserDTO dto = new ProjectMemberUserDTO();

        dto.id = member.getId();
        dto.user = PublicUserDTO.fromEntity(member.getUser());

        return dto;
    }

    public static ProjectMemberUserDTO fromEntityWithEmail(ProjectMember member) {
        ProjectMemberUserDTO dto = new ProjectMemberUserDTO();

        dto.id = member.getId();
        dto.user = PublicUserWithEmailDTO.fromEntity(member.getUser());

        return dto;
    }

    public static List<ProjectMemberUserDTO> fromEntities(Collection<ProjectMember> memberList) {
        List<ProjectMemberUserDTO> dtoList = new ArrayList<ProjectMemberUserDTO>(memberList.size());
        for (ProjectMember member : memberList) {
            dtoList.add(fromEntity(member));
        }
        return dtoList;
    }

    public static List<ProjectMemberUserDTO> fromEntitiesWithEmail(Collection<ProjectMember> memberList) {
        List<ProjectMemberUserDTO> dtoList = new ArrayList<ProjectMemberUserDTO>(memberList.size());
        for (ProjectMember member : memberList) {
            dtoList.add(fromEntityWithEmail(member));
        }
        return dtoList;
    }

    public static ProjectMemberUserDTO fromSearchHit(SearchHit hit, Long organizationId) throws IOException {
        ProjectMemberUserDTO dto = new ProjectMemberUserDTO();

        for (SearchHit memberHit : hit.getInnerHits().get(ElasticsearchUserDTO.PROJECTS)) {
            Map<String, Object> memberSource = JsonUtils.convertJsonToMapUsingLongForInts(memberHit.getSourceAsString());
            if (organizationId.equals((Long) memberSource.get(ElasticsearchNestedProjectDTO.PROJECT_ID))) {
                dto.id = (Long) memberSource.get(ElasticsearchNestedProjectDTO.MEMBER_ID);
                break;
            }
        }
        dto.user = PublicUserDTO.fromSearchHit(hit);

        return dto;
    }

    public static List<ProjectMemberUserDTO> fromSearchHits(SearchHits hits, Long organizationId, Long firstUserId)
            throws IOException {
        List<ProjectMemberUserDTO> dtoList = new ArrayList<ProjectMemberUserDTO>(hits.getHits().length);
        for (SearchHit hit : hits) {
            ProjectMemberUserDTO dto = fromSearchHit(hit, organizationId);
            if (dto.user.id.equals(firstUserId)) {
                dtoList.add(0, dto);
            } else {
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
}
