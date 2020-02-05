/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.services.dto;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.task.Attachment;

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
