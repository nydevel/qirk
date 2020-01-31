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

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskDTO extends IdDTO {

    @JsonProperty(value = "project")
    @NotNull(message = "project in TaskDTO must not be null", groups = OnCreate.class)
    @Valid
    public IdOrUiIdDTO project;

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in TaskDTO must not be null", groups = OnUpdate.class)
    public Long recordVersion;

    @NotBlank(message = "description in TaskDTO must not be blank")
    @Size(max = 10000, message = "description in TaskDTO must not be no more than 10000 characters")
    public String description;

    @JsonProperty(value = "assignee")
    public Long assigneeId;

    @JsonProperty(value = "assign_to_me")
    public boolean assignToMe = false;

    @JsonProperty(value = "task_type")
    public String typeNameCode = TaskType.DEFAULT.toString();

    @JsonProperty(value = "task_priority")
    public String priorityNameCode = TaskPriority.DEFAULT.toString();

    @JsonProperty(value = "task_status")
    @NotNull(message = "task_status in TaskDTO must not be null")
    public String statusNameCode = TaskStatus.DEFAULT.toString();

    @JsonProperty(value = "linked_tasks")
    @NotNull(message = "linked_tasks in TaskDTO must not be null", groups = OnCreate.class)
    public Set<Long> linkedTaskIds = new HashSet<Long>();

    @JsonProperty(value = "hashtag_ids")
    @NotNull(message = "hashtag_ids in TaskDTO must not be null")
    public Set<Long> hashtagIds = new HashSet<Long>();

    @JsonProperty(value = "hashtag_names")
    @NotNull(message = "hashtagNames in TaskDTO must not be null")
    public Set<@Pattern(regexp = RegExpPattern.SLUG_WITH_POINT
            + "{1,127}", message = "hashtag_names in TaskDTO must be slug") String> hashtagNames = new HashSet<String>();

    public void normalize() {
        if (typeNameCode == null) {
            typeNameCode = TaskType.DEFAULT.toString();
        }
        if (priorityNameCode == null) {
            priorityNameCode = TaskPriority.DEFAULT.toString();
        }

        typeNameCode = typeNameCode.strip().toUpperCase();
        priorityNameCode = priorityNameCode.strip().toUpperCase();
        statusNameCode = statusNameCode.strip().toUpperCase();
    }
}
