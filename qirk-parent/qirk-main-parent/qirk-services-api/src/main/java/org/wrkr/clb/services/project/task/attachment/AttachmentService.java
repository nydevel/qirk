package org.wrkr.clb.services.project.task.attachment;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.AttachmentDTO;
import org.wrkr.clb.services.dto.project.task.AttachmentCreateDTO;

@Validated
public interface AttachmentService {

    public AttachmentDTO create(
            @NotNull(message = "externalPath create must not be null") String externalPath,
            @NotNull(message = "task create must not be null") Task task);

    public List<AttachmentDTO> createFromTemporary(User currentUser, @Valid AttachmentCreateDTO createDTO) throws Exception;

    public List<AttachmentDTO> listByTask(User currentUser,
            @NotNull(message = "taskId listByTask must not be null") Long taskId);

    public void delete(User currentUser,
            @NotNull(message = "id delete must not be null") Long id) throws Exception;

    // public String getThumbnail(User currentUser,
    // @NotNull(message = "id delete must not be null") Long id) throws Exception;
}
