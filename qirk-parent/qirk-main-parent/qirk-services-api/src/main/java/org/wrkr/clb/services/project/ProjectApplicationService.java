package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.project.ProjectApplicationDTO;
import org.wrkr.clb.services.dto.project.ProjectApplicationReadDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberPermissionsDTO;

/**
 * @author Evgeny Poreykin
 *
 */
@Validated
public interface ProjectApplicationService {

    public ProjectApplicationReadDTO create(User currentUser, @Valid ProjectApplicationDTO projectApplicationDTO)
            throws Exception;

    public List<ProjectApplicationReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId in ProjectApplicationService must not be null") Long projectId);

    public List<ProjectApplicationReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId in ProjectApplicationService must not be null") String projectUiId);

    public ProjectApplicationReadDTO reject(User currentUser, @Valid RejectDTO rejectDTO) throws Exception;

    public void accept(User currentUser, @Valid ProjectMemberPermissionsDTO applicationDTO) throws Exception;

    public List<ProjectApplicationReadDTO> listByUser(User currentUser);

    public void cancel(User currentUser,
            @NotNull(message = "id in ProjectApplicationService must not be null") Long id) throws Exception;
}
