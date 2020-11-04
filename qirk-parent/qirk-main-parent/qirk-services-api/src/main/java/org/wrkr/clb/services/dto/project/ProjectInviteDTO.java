package org.wrkr.clb.services.dto.project;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnCreateByEmail;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Evgeny Poreykin
 *
 */
public class ProjectInviteDTO extends IdDTO {

    @JsonProperty(value = "user")
    @NotNull(message = "user in ProjectInviteDTO must not be null", groups = OnCreate.class)
    public Long userId;

    @JsonProperty(value = "project")
    @NotNull(message = "project in ProjectInviteDTO must not be null", groups = { OnCreate.class, OnCreateByEmail.class })
    public Long projectId;

    @NotNull(message = "text in ProjectInviteDTO must not be null", groups = OnCreate.class)
    @Null(message = "text in ProjectInviteDTO must be null", groups = OnCreateByEmail.class)
    public String text;
}
