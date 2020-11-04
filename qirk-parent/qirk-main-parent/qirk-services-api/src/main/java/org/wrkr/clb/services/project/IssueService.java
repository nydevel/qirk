package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.project.IssueDTO;
import org.wrkr.clb.services.dto.project.IssueReadDTO;


@Validated
public interface IssueService {

    @Validated(OnCreate.class)
    public IssueReadDTO create(User currentUser,
            @Valid IssueDTO issueDTO) throws Exception;

    @Validated(OnUpdate.class)
    public IssueReadDTO update(User currentUser,
            @Valid IssueDTO issueDTO) throws Exception;

    public IssueReadDTO get(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public List<IssueReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<IssueReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId must not be null") String projectUiId);

    public ChatPermissionsDTO getChatToken(User currentUser,
            @NotNull(message = "issueId must not be null") Long issueId) throws Exception;

}
