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
package org.wrkr.clb.services.dto.project;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MemoReadDTO extends IdDTO {

    public String body;

    @JsonProperty(value = "created_at")
    public String createdAt;

    public ProjectMemberUserDTO author;

    @JsonProperty(value = "can_delete")
    public boolean canDelete = false;

    public static MemoReadDTO fromEntity(Memo memo, boolean canDelete) {
        MemoReadDTO dto = new MemoReadDTO();

        dto.id = memo.getId();
        dto.body = memo.getBody();
        dto.createdAt = memo.getCreatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        dto.author = ProjectMemberUserDTO.fromEntity(memo.getAuthor());
        dto.canDelete = canDelete;

        return dto;
    }

    public static MemoReadDTO fromEntity(Memo memo, ProjectMember currentProjectMember) {
        boolean canDelete = (currentProjectMember != null &&
                (currentProjectMember.isManager() || currentProjectMember.getId().equals(memo.getAuthor().getId())));
        return fromEntity(memo, canDelete);
    }

    public static List<MemoReadDTO> fromEntities(List<Memo> memoList, ProjectMember currentProjectMember) {
        List<MemoReadDTO> dtoList = new ArrayList<MemoReadDTO>(memoList.size());
        for (Memo memo : memoList) {
            dtoList.add(fromEntity(memo, currentProjectMember));
        }
        return dtoList;
    }
}
