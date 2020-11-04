package org.wrkr.clb.services.dto;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.task.attachment.Attachment;

public class AttachmentDTO extends IdDTO {

    public String filename;

    public String url;

    public static AttachmentDTO fromEntity(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();

        dto.id = attachment.getId();
        dto.filename = attachment.getFilename();
        dto.url = "file/get/" + attachment.getId().toString() + "/" + attachment.getFilename();

        return dto;
    }

    public static List<AttachmentDTO> fromEntities(List<Attachment> attachmentList) {
        List<AttachmentDTO> dtoList = new ArrayList<AttachmentDTO>(attachmentList.size());
        for (Attachment attachment : attachmentList) {
            dtoList.add(fromEntity(attachment));
        }
        return dtoList;
    }
}
