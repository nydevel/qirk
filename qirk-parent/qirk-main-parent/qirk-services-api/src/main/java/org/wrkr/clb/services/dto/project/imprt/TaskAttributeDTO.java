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
package org.wrkr.clb.services.dto.project.imprt;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskAttributeDTO {

    @JsonProperty(value = "name_code")
    public String nameCode;
    @JsonProperty(value = "default")
    public boolean isDefault;

    public static TaskAttributeDTO fromEnum(TaskType.Type type) {
        TaskAttributeDTO dto = new TaskAttributeDTO();

        dto.nameCode = type.toString();
        dto.isDefault = TaskType.DEFAULT.equals(type);

        return dto;
    }

    public static List<TaskAttributeDTO> fromTypes(TaskType.Type[] types) {
        List<TaskAttributeDTO> dtoList = new ArrayList<TaskAttributeDTO>(types.length);
        for (TaskType.Type type : types) {
            if (!type.isDeprecated()) {
                dtoList.add(fromEnum(type));
            }
        }
        return dtoList;
    }

    public static TaskAttributeDTO fromEnum(TaskPriority.Priority priority) {
        TaskAttributeDTO dto = new TaskAttributeDTO();

        dto.nameCode = priority.toString();
        dto.isDefault = TaskPriority.DEFAULT.equals(priority);

        return dto;
    }

    public static List<TaskAttributeDTO> fromPriorities(TaskPriority.Priority[] priorities) {
        List<TaskAttributeDTO> dtoList = new ArrayList<TaskAttributeDTO>(priorities.length);
        for (TaskPriority.Priority priority : priorities) {
            if (!priority.isDeprecated()) {
                dtoList.add(fromEnum(priority));
            }
        }
        return dtoList;
    }

    public static TaskAttributeDTO fromEnum(TaskStatus.Status status) {
        TaskAttributeDTO dto = new TaskAttributeDTO();

        dto.nameCode = status.toString();
        dto.isDefault = TaskStatus.DEFAULT.equals(status);

        return dto;
    }

    public static List<TaskAttributeDTO> fromStatuses(TaskStatus.Status[] statuses) {
        List<TaskAttributeDTO> dtoList = new ArrayList<TaskAttributeDTO>(statuses.length);
        for (TaskStatus.Status status : statuses) {
            dtoList.add(fromEnum(status));
        }
        return dtoList;
    }
}
