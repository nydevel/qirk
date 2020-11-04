package org.wrkr.clb.services.project.roadmap;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.MoveDTO;
import org.wrkr.clb.services.dto.project.roadmap.RoadDTO;
import org.wrkr.clb.services.dto.project.roadmap.RoadReadDTO;


@Validated
public interface RoadService {

    @Validated(OnCreate.class)
    public RoadReadDTO create(User currentUser, @Valid RoadDTO roadDTO) throws Exception;

    @Validated(OnUpdate.class)
    public RoadReadDTO update(User currentUser, @Valid RoadDTO roadDTO) throws Exception;

    @Validated(OnUpdate.class)
    public void move(User currentUser, @Valid MoveDTO moveDTO) throws Exception;

    public List<RoadReadDTO> list(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<RoadReadDTO> list(User currentUser,
            @NotNull(message = "projectUiId must not be null") String projectUiId);

    public void delete(User currentUser, @NotNull(message = "id must not be null") Long id) throws Exception;
}
