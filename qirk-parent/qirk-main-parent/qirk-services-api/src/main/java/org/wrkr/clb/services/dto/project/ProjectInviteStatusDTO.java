package org.wrkr.clb.services.dto.project;

import org.wrkr.clb.model.project.BaseProjectInvite;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Evgeny Poreykin
 *
 */
public class ProjectInviteStatusDTO extends IdDTO {

    @JsonInclude(Include.NON_NULL)
    public InviteStatus status;

    public static ProjectInviteStatusDTO fromInvite(BaseProjectInvite invite) {
        ProjectInviteStatusDTO dto = new ProjectInviteStatusDTO();

        if (invite != null) {
            dto.id = invite.getId();
            dto.status = invite.getStatus();
        }

        return dto;
    }
}
