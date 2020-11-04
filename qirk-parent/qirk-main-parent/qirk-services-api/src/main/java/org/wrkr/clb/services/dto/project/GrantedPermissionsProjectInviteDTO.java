package org.wrkr.clb.services.dto.project;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.wrkr.clb.common.validation.groups.OnCreateByEmail;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Evgeny Poreykin
 *
 */
public class GrantedPermissionsProjectInviteDTO extends ProjectInviteDTO {

    @NotEmpty(message = "email in GrantedPermissionsProjectInviteDTO must not be empty", groups = OnCreateByEmail.class)
    @Email(message = "email in GrantedPermissionsProjectInviteDTO must be valid", groups = OnCreateByEmail.class)
    public String email;

    @JsonProperty(value = "write_allowed")
    @NotNull(message = "write_allowed in GrantedPermissionsProjectInviteDTO must not be null")
    public Boolean writeAllowed;

    @NotNull(message = "manager in GrantedPermissionsProjectInviteDTO must not be null")
    public Boolean manager;
}
