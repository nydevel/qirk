package org.wrkr.clb.services.dto.project;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.BaseProjectInvite;
import org.wrkr.clb.model.project.ProjectInvite;
import org.wrkr.clb.services.dto.user.PublicUserDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Evgeny Poreykin
 *
 */
public class ProjectInviteReadDTO extends ProjectInviteStatusDTO {

    @JsonInclude(Include.NON_NULL)
    public PublicUserDTO user;

    @JsonInclude(Include.NON_NULL)
    public ProjectNameAndUiIdDTO project;

    public String text;

    @JsonProperty(value = "created_at")
    public String createdAt;

    @JsonProperty(value = "updated_at")
    public String updatedAt;

    public static ProjectInviteReadDTO fromInvite(BaseProjectInvite invite,
            boolean includeUser, boolean includeProject, boolean includeStatus) {
        ProjectInviteReadDTO dto = new ProjectInviteReadDTO();
        dto.setFields(invite, includeUser, includeProject, includeStatus);
        return dto;
    }

    public static ProjectInviteReadDTO frominvite(BaseProjectInvite invite) {
        return fromInvite(invite, false, false, false);
    }

    public static final List<ProjectInviteReadDTO> fromInvites(List<ProjectInvite> inviteList,
            boolean includeUser, boolean includeProject, boolean includeStatus) {
        List<ProjectInviteReadDTO> dtoList = new ArrayList<ProjectInviteReadDTO>();
        for (ProjectInvite invite : inviteList) {
            dtoList.add(fromInvite(invite, includeUser, includeProject, includeStatus));
        }
        return dtoList;
    }

    public static List<ProjectInviteReadDTO> fromInvitesForUser(List<ProjectInvite> inviteList) {
        return fromInvites(inviteList, false, true, true);
    }

    public static List<ProjectInviteReadDTO> fromInvitesForProject(List<ProjectInvite> inviteList) {
        return fromInvites(inviteList, true, false, true);
    }

    protected void setFields(BaseProjectInvite invite, boolean includeUser, boolean includeProject, boolean includeStatus) {
        id = invite.getId();
        text = invite.getText();
        createdAt = invite.getCreatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        updatedAt = invite.getUpdatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);

        if (includeUser) {
            user = PublicUserDTO.fromEntity(invite.getUser());
        }
        if (includeProject) {
            project = ProjectNameAndUiIdDTO.fromEntity(invite.getProject());
        }
        if (includeStatus) {
            status = invite.getStatus();
        }
    }
}
