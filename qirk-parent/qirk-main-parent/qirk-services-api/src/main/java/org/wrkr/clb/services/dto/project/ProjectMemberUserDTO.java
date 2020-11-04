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
import org.wrkr.clb.services.dto.user.AccountUserDTO;

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
        dto.user = AccountUserDTO.fromEntity(member.getUser());

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

    public static ProjectMemberUserDTO fromSearchHit(SearchHit hit, Long projectId) throws IOException {
        ProjectMemberUserDTO dto = new ProjectMemberUserDTO();

        for (SearchHit memberHit : hit.getInnerHits().get(ElasticsearchUserDTO.PROJECTS)) {
            Map<String, Object> memberSource = JsonUtils.convertJsonToMapUsingLongForInts(memberHit.getSourceAsString());
            if (projectId.equals((Long) memberSource.get(ElasticsearchNestedProjectDTO.PROJECT_ID))) {
                dto.id = (Long) memberSource.get(ElasticsearchNestedProjectDTO.MEMBER_ID);
                break;
            }
        }
        dto.user = PublicUserDTO.fromSearchHit(hit);

        return dto;
    }

    public static List<ProjectMemberUserDTO> fromSearchHits(SearchHits hits, Long projectId, Long firstUserId)
            throws IOException {
        List<ProjectMemberUserDTO> dtoList = new ArrayList<ProjectMemberUserDTO>(hits.getHits().length);
        for (SearchHit hit : hits) {
            ProjectMemberUserDTO dto = fromSearchHit(hit, projectId);
            if (dto.user.id.equals(firstUserId)) {
                dtoList.add(0, dto);
            } else {
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
}
