package org.wrkr.clb.services.user;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.MoveDTO;
import org.wrkr.clb.services.dto.user.UserFavoriteDTO;
import org.wrkr.clb.services.dto.user.UserFavoriteReadDTO;

@Validated
public interface UserFavoriteService {

    UserFavoriteReadDTO create(User currentUser,
            @Valid UserFavoriteDTO userFavoriteDTO) throws Exception;

    @Validated(OnUpdate.class)
    UserFavoriteReadDTO move(User currentUser,
            @Valid MoveDTO moveDTO) throws Exception;

    List<UserFavoriteReadDTO> listByUser(User currentUser);

    void deleteById(User currentUser,
            @NotNull(message = "id not be null") Long id) throws Exception;

    void deleteByUserAndProjectId(User user, @NotNull(message = "projectId must not be null") Long projectId);

    void deleteByUserAndProjectUiId(User user,
            @NotNull(message = "projectUiId must not be null") String projectUiId);

    void deleteByUserIdAndProject(
            @NotNull(message = "userId must not be null") Long userId,
            @NotNull(message = "project must not be null") Project project);
}
