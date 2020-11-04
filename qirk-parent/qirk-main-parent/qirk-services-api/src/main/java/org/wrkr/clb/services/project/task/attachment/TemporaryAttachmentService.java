package org.wrkr.clb.services.project.task.attachment;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.Project;

@Validated
public interface TemporaryAttachmentService {

    public String create(@NotNull(message = "externalPath create must not be null") String externalPath,
            @NotNull(message = "task create must not be null") Project project);

    public void clearTemporaryAttachments();
}
