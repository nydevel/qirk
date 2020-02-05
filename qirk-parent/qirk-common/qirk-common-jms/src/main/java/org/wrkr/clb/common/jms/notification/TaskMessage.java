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
package org.wrkr.clb.common.jms.notification;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskMessage extends BaseTaskNotificationMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(TaskMessage.class);

    public static final String ORGANIZATION_ID = "organization_id";
    public static final String ORGANIZATION_UI_ID = "organization_ui_id";
    public static final String PROJECT_ID = "project_id";
    public static final String PROJECT_UI_ID = "project_ui_id";
    public static final String PROJECT_NAME = "project_name";
    public static final String TASK_ID = "task_id";
    public static final String TASK_NUMBER = "task_number";
    public static final String TASK_SUMMARY = "task_summary";
    public static final String UPDATED_BY_USER_ID = "updated_by";
    public static final String UPDATED_BY_USERNAME = "updated_by_username";
    public static final String UPDATED_BY_FULL_NAME = "updated_by_full_name";
    public static final String UPDATED_AT = "updated_at";

    public static final String NEW_ASSIGNEE = "new_assignee";
    public static final String NEW_ASSIGNEE_USERNAME = "new_assignee_username";
    public static final String NEW_ASSIGNEE_FULL_NAME = "new_assignee_full_name";

    public static final String NEW_TYPE = "new_type";
    public static final String NEW_PRIORITY = "new_priority";
    public static final String NEW_STATUS = "new_status";

    public static final String NEW_TYPE_HUMAN_READABLE = "new_type_hr";
    public static final String NEW_PRIORITY_HUMAN_READABLE = "new_priority_hr";
    public static final String NEW_STATUS_HUMAN_READABLE = "new_status_hr";

    @JsonProperty(value = ORGANIZATION_ID)
    public long organizationId;
    @JsonProperty(value = ORGANIZATION_UI_ID)
    public String organizationUiId;
    @JsonProperty(value = PROJECT_ID)
    public long projectId;
    @JsonProperty(value = PROJECT_UI_ID)
    public String projectUiId;
    @JsonProperty(value = PROJECT_NAME)
    public String projectName;
    @JsonProperty(value = TASK_ID)
    public long taskId;
    @JsonProperty(value = TASK_NUMBER)
    public long taskNumber;
    @JsonProperty(value = TASK_SUMMARY)
    public String taskSummary;
    @JsonProperty(value = UPDATED_BY_USER_ID)
    public long updatedByUserId;
    @JsonProperty(value = UPDATED_BY_USERNAME)
    public String updatedByUsername;
    @JsonProperty(value = UPDATED_BY_FULL_NAME)
    public String updatedByFullName;
    @JsonProperty(value = UPDATED_AT)
    public String updatedAt;

    @JsonProperty(value = NEW_ASSIGNEE)
    public Long newAssignee;
    @JsonProperty(value = NEW_ASSIGNEE_USERNAME)
    public String newAssigneeUsername;
    @JsonProperty(value = NEW_ASSIGNEE_FULL_NAME)
    public String newAssigneeFullName;

    @JsonProperty(value = NEW_TYPE)
    public String newType;
    @JsonProperty(value = NEW_PRIORITY)
    public String newPriority;
    @JsonProperty(value = NEW_STATUS)
    public String newStatus;

    @JsonProperty(value = NEW_TYPE_HUMAN_READABLE)
    public String newTypeHumanReadable;
    @JsonProperty(value = NEW_PRIORITY_HUMAN_READABLE)
    public String newPriorityHumanReadable;
    @JsonProperty(value = NEW_STATUS_HUMAN_READABLE)
    public String newStatusHumanReadable;

    protected TaskMessage(String type) {
        super(type);
    }

    protected TaskMessage(String type, Collection<Long> subscriberIds, Collection<String> subscriberEmails) {
        super(type, subscriberIds, subscriberEmails);
    }

    public TaskMessage(Collection<Long> subscriberIds, Collection<String> subscriberEmails) {
        super(BaseNotificationMessage.Type.TASK_CREATED, subscriberIds, subscriberEmails);
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
