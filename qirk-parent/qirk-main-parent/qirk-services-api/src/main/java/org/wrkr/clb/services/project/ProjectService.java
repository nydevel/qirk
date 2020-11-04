package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.project.ProjectDTO;
import org.wrkr.clb.services.dto.project.ProjectDocDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteOptionDTO;
import org.wrkr.clb.services.dto.project.ProjectNameAndUiIdDTO;
import org.wrkr.clb.services.dto.project.ProjectReadDTO;

@Validated
public interface ProjectService {

    public ExistsDTO checkUiId(User currentUser,
            @NotNull(message = "uiId must not be null") @Pattern(regexp = RegExpPattern.SLUG
                    + "+", message = "uiId must be slug") String uiId)
            throws Exception;

    public Project create(User creatorUser, @Valid ProjectDTO projectDTO,
            @NotNull(message = "membersToCreate must not be null") List<User> membersToCreate) throws Exception;

    @Validated(OnCreate.class)
    public ProjectReadDTO create(User currentUser, @Valid ProjectDTO projectDTO) throws Exception;

    @Validated(OnUpdate.class)
    public ProjectReadDTO update(User currentUser, @Valid ProjectDTO projectDTO) throws Exception;

    public ProjectDocDTO getDocumentation(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public ProjectDocDTO getDocumentationByUiId(User currentUser,
            @NotNull(message = "uiId must not be null") String uiId) throws Exception;

    public ProjectReadDTO updateDocumentation(User currentUser, @Valid ProjectDocDTO documentationDTO) throws Exception;

    public ProjectReadDTO get(User currentUser,
            @NotNull(message = "id must not be null") Long id,
            boolean includeApplication) throws Exception;

    public ProjectReadDTO getByUiId(User currentUser,
            @NotNull(message = "uiId must not be null") String uiId,
            boolean includeApplication) throws Exception;

    public List<ProjectNameAndUiIdDTO> listManagedByUser(User currentUser);

    public List<ProjectNameAndUiIdDTO> listByUser(User currentUser);

    @Deprecated
    public List<ProjectInviteOptionDTO> listInviteOptions(User currentUser, long userId);

    public List<ProjectNameAndUiIdDTO> listAvailableToUser(User user);

    public ChatPermissionsDTO getChatToken(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;
}
