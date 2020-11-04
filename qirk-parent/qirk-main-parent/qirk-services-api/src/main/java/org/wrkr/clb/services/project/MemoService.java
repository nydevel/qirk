package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.MemoDTO;
import org.wrkr.clb.services.dto.project.MemoReadDTO;


@Validated
public interface MemoService {

    MemoReadDTO create(User currentUser,
            @Valid MemoDTO memoDTO) throws Exception;

    List<MemoReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId listByProject must not be null") Long projectId);
    
    List<MemoReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId listByProject must not be null") String projectUiId);

    void delete(User currentUser,
            @NotNull(message = "id delete must not be null") Long id) throws Exception;
}
