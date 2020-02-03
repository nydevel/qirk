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
package org.wrkr.clb.common.jms.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskCommentMessage extends BaseTaskNotificationMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(TaskCommentMessage.class);

    @JsonIgnore
    public static final String SENDER_USERNAME = "sender_username";
    @JsonIgnore
    public static final String SENDER_FULL_NAME = "sender_full_name";

    @JsonIgnore
    public static final String ORGANIZATION_ID = "organization_id";
    @JsonIgnore
    public static final String PROJECT_ID = "project_id";
    @JsonIgnore
    public static final String PROJECT_NAME = "project_name";
    @JsonIgnore
    public static final String TASK_ID = "task_id";
    @JsonIgnore
    public static final String TASK_NUMBER = "task_number";
    @JsonIgnore
    public static final String TASK_SUMMARY = "task_summary";

    @JsonIgnore
    public static final String MESSAGE = "message";
    @JsonIgnore
    public static final String COMMENTED_AT = "commented_at";

    @JsonProperty(value = SENDER_USERNAME)
    public String senderUsername;
    @JsonProperty(value = SENDER_FULL_NAME)
    public String senderFullName;

    @JsonProperty(value = ORGANIZATION_ID)
    public long organizationId;
    @JsonProperty(value = PROJECT_ID)
    public long projectId;
    @JsonProperty(value = PROJECT_NAME)
    public String projectName;
    @JsonProperty(value = TASK_ID)
    public long taskId;
    @JsonProperty(value = TASK_NUMBER)
    public long taskNumber;
    @JsonProperty(value = TASK_SUMMARY)
    public String taskSummary;

    @JsonProperty(value = MESSAGE)
    public String message;
    @JsonProperty(value = COMMENTED_AT)
    public String commentedAt;

    public TaskCommentMessage() {
        super(BaseNotificationMessage.Type.TASK_COMMENTED);
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
