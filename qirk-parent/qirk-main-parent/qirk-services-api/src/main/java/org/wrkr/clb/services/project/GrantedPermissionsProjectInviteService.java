package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnCreateByEmail;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.TokenRejectDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteReadDTO;
import org.wrkr.clb.services.dto.user.TokenRegisterDTO;


@Validated
public interface GrantedPermissionsProjectInviteService {

    @Validated(OnCreate.class)
    public GrantedPermissionsProjectInviteReadDTO create(User currentUser,
            @Valid GrantedPermissionsProjectInviteDTO projectInviteDTO) throws Exception;

    @Validated(OnCreateByEmail.class)
    public GrantedPermissionsProjectInviteReadDTO createByEmail(User currentUser,
            @Valid GrantedPermissionsProjectInviteDTO projectInviteDTO)
            throws Exception;

    @Validated(OnUpdate.class)
    public GrantedPermissionsProjectInviteReadDTO update(User currentUser,
            @Valid GrantedPermissionsProjectInviteDTO projectInviteDTO) throws Exception;

    public GrantedPermissionsProjectInviteReadDTO get(User currentUser,
            @NotNull(message = "id must not be null") Long id)
            throws Exception;

    public List<GrantedPermissionsProjectInviteReadDTO> listByUser(User currentUser);

    public void accept(User currentUser,
            @NotNull(message = "id must not be null") Long id)
            throws Exception;

    public void acceptByToken(@Valid TokenRegisterDTO dto) throws Exception;

    public GrantedPermissionsProjectInviteReadDTO reject(User currentUser, @Valid RejectDTO rejectDTO) throws Exception;

    public GrantedPermissionsProjectInviteReadDTO rejectByToken(@Valid TokenRejectDTO rejectDTO) throws Exception;

    public List<GrantedPermissionsProjectInviteReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId,
            boolean includeEmail);

    public List<GrantedPermissionsProjectInviteReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId must not be null") String projectUiId,
            boolean includeEmail);

    public void cancel(User currentUser,
            @NotNull(message = "id must not be null") Long id);
}
