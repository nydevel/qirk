package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnCreateBatch;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberListDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberUserDTO;

@Validated
public interface ProjectMemberService {

    public ProjectMember create(
            @NotNull(message = "user must not be null") User user,
            @NotNull(message = "project must not be null") Project project,
            @Valid ProjectMemberDTO projectMemberDTO);

    @Validated(OnCreate.class)
    public ProjectMemberReadDTO create(User currentUser, @Valid ProjectMemberDTO projectMemberDTO) throws Exception;

    @Validated(OnCreateBatch.class)
    public List<ProjectMemberReadDTO> createBatch(User currentUser, @Valid ProjectMemberListDTO projectMemberListDTO)
            throws Exception;

    public ProjectMemberReadDTO get(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    @Validated(OnUpdate.class)
    public ProjectMemberReadDTO update(User currentUser, @Valid ProjectMemberDTO projectMemberDTO) throws Exception;

    public List<ProjectMemberReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<ProjectMemberReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId must not be null") String projectUiId);

    public void delete(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public void leave(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId) throws Exception;

    public List<ProjectMemberUserDTO> search(User currentUser,
            @NotNull(message = "prefix must not be null") String prefix,
            @Valid IdOrUiIdDTO projectDTO, boolean meFirst)
            throws Exception;
}
