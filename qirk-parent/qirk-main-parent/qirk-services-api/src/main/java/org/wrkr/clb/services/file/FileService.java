package org.wrkr.clb.services.file;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.commons.fileupload.FileItem;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.AttachmentDTO;

@Validated
public interface FileService {

    public AttachmentDTO uploadAndCreateAttachment(User currentUser,
            @NotNull(message = "file must not be null") FileItem file,
            @NotNull(message = "taskId must not be null") Long taskId) throws Exception;

    public String uploadAndCreateTemporaryAttachment(User currentUser,
            @NotNull(message = "file must not be null") FileItem file,
            @NotNull(message = "projectId must not be null") Long projectId) throws Exception;

    public String getTemporaryLink(Attachment attachment) throws Exception;

    public String getErrorCodeFromException(Exception e) throws IOException;
}
