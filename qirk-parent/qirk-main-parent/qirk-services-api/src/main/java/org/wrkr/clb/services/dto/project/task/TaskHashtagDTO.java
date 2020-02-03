/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.services.dto.project.task;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class TaskHashtagDTO extends IdDTO {

    public String name;

    @JsonInclude(Include.NON_NULL)
    public Boolean used;

    public static TaskHashtagDTO fromEntity(TaskHashtag hashtag) {
        TaskHashtagDTO dto = new TaskHashtagDTO();

        dto.id = hashtag.getId();
        dto.name = hashtag.getName();
        if (hashtag.getTasksCount() != null) {
            dto.used = (hashtag.getTasksCount() != 0);
        }

        return dto;
    }

    public static List<TaskHashtagDTO> fromEntities(List<TaskHashtag> hashtagList) {
        List<TaskHashtagDTO> dtoList = new ArrayList<TaskHashtagDTO>(hashtagList.size());
        for (TaskHashtag hashtag : hashtagList) {
            dtoList.add(fromEntity(hashtag));
        }
        return dtoList;
    }
}
