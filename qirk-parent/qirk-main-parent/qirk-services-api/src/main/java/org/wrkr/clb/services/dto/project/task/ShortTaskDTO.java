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
package org.wrkr.clb.services.dto.project.task;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShortTaskDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @JsonInclude(Include.NON_NULL)
    public Long recordVersion;

    @JsonInclude(Include.NON_NULL)
    public Long number;

    @JsonInclude(Include.NON_NULL)
    public String summary;

    @JsonProperty(value = "task_priority")
    @JsonInclude(Include.NON_NULL)
    public TaskPriority priority;

    @JsonProperty(value = "task_status")
    @JsonInclude(Include.NON_NULL)
    public TaskStatus status;

    public static ShortTaskDTO fromEntity(Task task) {
        ShortTaskDTO dto = new ShortTaskDTO();

        dto.id = task.getId();
        dto.number = task.getNumber();
        dto.summary = task.getSummary();

        return dto;
    }

    public static ShortTaskDTO fromEntityWithRecordVersionAndPriorityAndStatus(Task task) {
        ShortTaskDTO dto = fromEntity(task);

        dto.recordVersion = task.getRecordVersion();
        dto.priority = task.getPriority();
        dto.status = task.getStatus();

        return dto;
    }

    public static List<ShortTaskDTO> fromEntities(List<Task> taskList) {
        List<ShortTaskDTO> dtoList = new ArrayList<ShortTaskDTO>();
        for (Task task : taskList) {
            dtoList.add(fromEntity(task));
        }
        return dtoList;
    }

    public static List<ShortTaskDTO> fromEntitiesWithRecordVersionAndPriorityAndStatus(List<Task> taskList) {
        List<ShortTaskDTO> dtoList = new ArrayList<ShortTaskDTO>();
        for (Task task : taskList) {
            dtoList.add(fromEntityWithRecordVersionAndPriorityAndStatus(task));
        }
        return dtoList;
    }
}
