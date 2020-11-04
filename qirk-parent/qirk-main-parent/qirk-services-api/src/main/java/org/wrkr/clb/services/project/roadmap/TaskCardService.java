package org.wrkr.clb.services.project.roadmap;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.VersionedEntityDTO;
import org.wrkr.clb.services.dto.project.MoveToRoadDTO;
import org.wrkr.clb.services.dto.project.roadmap.TaskCardDTO;
import org.wrkr.clb.services.dto.project.roadmap.TaskCardReadDTO;


@Validated
public interface TaskCardService {

    public TaskCardReadDTO create(User currentUser, @Valid TaskCardDTO cardDTO) throws Exception;

    public TaskCardReadDTO update(User currentUser, @Valid TaskCardDTO cardDTO) throws Exception;

    public void move(User currentUser, @Valid MoveToRoadDTO moveDTO) throws Exception;

    public List<TaskCardReadDTO> list(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<TaskCardReadDTO> list(User currentUser,
            @NotNull(message = "projectUiId must not be null") String projectUiId);

    public void archive(User currentUser, @Valid VersionedEntityDTO dto) throws Exception;

    public void archiveBatchByRoadId(Long roadId);

    public List<TaskCardReadDTO> listArchive(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<TaskCardReadDTO> listArchive(User currentUser,
            @NotNull(message = "projectUiId must not be null") String projectUiId);

    public void delete(User currentUser,
            @NotNull(message = "projectId must not be null") Long id) throws Exception;
}
