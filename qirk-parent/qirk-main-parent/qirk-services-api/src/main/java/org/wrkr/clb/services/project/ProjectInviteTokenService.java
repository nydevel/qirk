package org.wrkr.clb.services.project;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.ProjectInviteToken;
import org.wrkr.clb.services.dto.user.RegisteredDTO;


@Validated
public interface ProjectInviteTokenService {

    public RegisteredDTO checkToken(@NotNull(message = "id token ProjectInviteTokenService must not be null") String token);

    public ProjectInviteToken create(
            @NotNull(message = "invite token ProjectInviteTokenService must not be null") GrantedPermissionsProjectInvite invite,
            @NotEmpty(message = "email token ProjectInviteTokenService must not be empty") @Email(message = "email token ProjectInviteTokenService must be valid") String email);
}
