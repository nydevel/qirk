package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberPermissionsDTO;


@Validated
public interface ProjectInviteService {

    public ProjectInviteReadDTO create(User currentUser, @Valid ProjectInviteDTO projectInviteDTO) throws Exception;

    public ProjectInviteReadDTO get(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public List<ProjectInviteReadDTO> listByUser(User currentUser);

    public ProjectInviteReadDTO accept(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public ProjectInviteReadDTO reject(User currentUser, @Valid RejectDTO rejectDTO) throws Exception;

    public List<ProjectInviteReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<ProjectInviteReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId must not be null") String projectUiId);

    public void cancel(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public void execute(User currentUser, @Valid ProjectMemberPermissionsDTO inviteDTO) throws Exception;
}
