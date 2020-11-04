package org.wrkr.clb.services.project.task.attachment;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;

@Validated
public interface AttachmentFileService {

    public String getTemporaryLink(User currentUser,
            @NotNull(message = "id must not be null") Long id);
}
