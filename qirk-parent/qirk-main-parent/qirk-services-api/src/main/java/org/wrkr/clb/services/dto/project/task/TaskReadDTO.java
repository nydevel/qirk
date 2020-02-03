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

import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.NameAndUiIdDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberUserDTO;
import org.wrkr.clb.services.dto.project.ProjectWithOrganizationDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskReadDTO extends IdDTO {

    public Long number;

    @JsonInclude(Include.NON_NULL)
    public String summary;

    @JsonProperty(value = "record_version")
    @JsonInclude(Include.NON_NULL)
    public Long recordVersion;

    @JsonProperty(value = "description_md")
    @JsonInclude(Include.NON_NULL)
    public String descriptionMd;

    @JsonProperty(value = "description_html")
    @JsonInclude(Include.NON_NULL)
    public String descriptionHtml;

    @JsonProperty(value = "created_at")
    @JsonInclude(Include.NON_NULL)
    public String createdAt;

    @JsonProperty(value = "updated_at")
    @JsonInclude(Include.NON_NULL)
    public String updatedAt;

    @JsonInclude(Include.NON_NULL)
    public IdDTO project;

    @JsonProperty(value = "has_dropbox_settings")
    @JsonInclude(Include.NON_NULL)
    public Boolean hasDropboxSettings;

    @JsonProperty(value = "has_organization_dropbox_settings")
    @JsonInclude(Include.NON_NULL)
    public Boolean hasOrganizationDropboxSettings;

    @JsonInclude(Include.NON_NULL)
    public OrganizationMemberUserDTO reporter;

    @JsonInclude(Include.NON_NULL)
    public OrganizationMemberUserDTO assignee;

    @JsonProperty(value = "task_type")
    @JsonInclude(Include.NON_NULL)
    public TaskType taskType;

    @JsonProperty(value = "task_priority")
    @JsonInclude(Include.NON_NULL)
    public TaskPriority taskPriority;

    @JsonProperty(value = "task_status")
    @JsonInclude(Include.NON_NULL)
    public TaskStatus taskStatus;

    @JsonProperty(value = "linked_tasks")
    @JsonInclude(Include.NON_NULL)
    public List<LinkedTaskDTO> linkedTasks;

    @JsonInclude(Include.NON_NULL)
    public List<TaskHashtag> hashtags;

    @JsonInclude(Include.NON_NULL)
    public Boolean subscribed;

    @JsonProperty(value = "subscribers_count")
    @JsonInclude(Include.NON_NULL)
    public Long subscribersCount;

    public static TaskReadDTO fromEntity(Task task, boolean excludeFieldsForGet, boolean includeTimestamps,
            boolean includeProject, boolean includeReporterAndAssignee, boolean includeTypeAndPriorityAndStatus) {
        TaskReadDTO dto = new TaskReadDTO();

        dto.id = task.getId();
        dto.recordVersion = task.getRecordVersion();
        dto.number = task.getNumber();
        dto.summary = task.getSummary();

        if (!excludeFieldsForGet) {
            dto.descriptionMd = task.getDescriptionMd();
            dto.descriptionHtml = task.getDescriptionHtml();
        }

        if (includeTimestamps) {
            dto.createdAt = task.getCreatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);
            dto.updatedAt = task.getUpdatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        }

        if (includeProject) {
            dto.project = NameAndUiIdDTO.fromEntity(task.getProject());
        }

        if (includeReporterAndAssignee) {
            dto.reporter = OrganizationMemberUserDTO.fromEntity(task.getReporter());
            if (task.getAssignee() != null) {
                dto.assignee = OrganizationMemberUserDTO.fromEntity(task.getAssignee());
            }
        }

        if (includeTypeAndPriorityAndStatus) {
            dto.taskType = task.getType();
            dto.taskPriority = task.getPriority();
            dto.taskStatus = task.getStatus();
        }

        return dto;
    }

    public static TaskReadDTO fromEntity(Task task, boolean excludeFieldsForGet, boolean includeProject,
            boolean includeEverythingForRead) {
        return fromEntity(task, excludeFieldsForGet, includeEverythingForRead,
                includeProject, includeEverythingForRead, includeEverythingForRead);
    }

    public static TaskReadDTO fromEntity(Task task) {
        return fromEntity(task, true, false, false);
    }

    public static TaskReadDTO fromEntityWithEverythingForRead(Task task) {
        TaskReadDTO dto = fromEntity(task, false, true, true);
        dto.hashtags = task.getHashtags();
        return dto;
    }

    public static TaskReadDTO fromEntityWithEverythingForReadAndLinkedTasks(Task task, List<Task> linkedTasks) {
        TaskReadDTO dto = fromEntityWithEverythingForRead(task);
        dto.linkedTasks = LinkedTaskDTO.fromEntities(linkedTasks);
        return dto;
    }

    public static TaskReadDTO fromEntityWithEverythingForReadAndOrganizationAndSubscription(Task task) {
        TaskReadDTO dto = fromEntity(task, false, false, true);

        dto.project = ProjectWithOrganizationDTO.fromEntityWithOrganizationName(task.getProject());
        dto.hasDropboxSettings = (task.getProject().getDropboxSettingsId() != null);
        dto.hasOrganizationDropboxSettings = (task.getProject().getOrganization().getDropboxSettingsId() != null);
        dto.linkedTasks = LinkedTaskDTO.fromEntities(task.getLinkedTasks());
        dto.hashtags = task.getHashtags();
        dto.subscribed = false;

        return dto;
    }

    public static List<TaskReadDTO> fromEntities(List<Task> taskList, boolean includeTimestamps,
            boolean includeProject, boolean includeReporterAndAssignee, boolean includeTypeAndPriorityAndStatus) {
        List<TaskReadDTO> dtoList = new ArrayList<TaskReadDTO>(taskList.size());
        for (Task task : taskList) {
            dtoList.add(fromEntity(task, true,
                    includeTimestamps, includeProject, includeReporterAndAssignee, includeTypeAndPriorityAndStatus));
        }
        return dtoList;
    }

    public static List<TaskReadDTO> fromEntitiesWithoutOtherFields(List<Task> taskList) {
        return fromEntities(taskList, false, false, false, false);
    }

    public static List<TaskReadDTO> fromEntitiesWithoutTimestamps(List<Task> taskList) {
        return fromEntities(taskList, false, false, false, true);
    }

    @Deprecated
    public static List<TaskReadDTO> fromEntities(List<Task> taskList) {
        return fromEntities(taskList, true, false, false, true);
    }

    @Deprecated
    public static List<TaskReadDTO> fromEntitiesWithProject(List<Task> taskList) {
        return fromEntities(taskList, true, true, false, true);
    }

    public static List<TaskReadDTO> fromEntitiesWithEverythingForRead(List<Task> taskList) {
        return fromEntities(taskList, true, true, true, true);
    }
}
