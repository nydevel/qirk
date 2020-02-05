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
package org.wrkr.clb.common.jms.statistics;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NewTaskMessage extends BaseStatisticsMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(NewTaskMessage.class);

    public static final String PROJECT_ID = "project_id";
    public static final String PROJECT_NAME = "project_name";
    public static final String TASK_ID = "task_id";
    public static final String CREATED_AT = "created_at";
    public static final String TYPE = "type";
    public static final String PRIORITY = "priority";
    public static final String STATUS = "status";

    @JsonProperty(value = PROJECT_ID)
    public long projectId;
    @JsonProperty(value = PROJECT_NAME)
    public String projectName;
    @JsonProperty(value = TASK_ID)
    public long taskId;
    @JsonProperty(value = CREATED_AT)
    public long createdAt;
    @JsonProperty(value = TYPE)
    public String type;
    @JsonProperty(value = PRIORITY)
    public String priority;
    @JsonProperty(value = STATUS)
    public String status;

    public NewTaskMessage(long projectId, String projectName, long taskId, OffsetDateTime createdAt,
            String type, String priority, String status) {
        super(Code.NEW_TASK);
        this.projectId = projectId;
        this.projectName = projectName;
        this.taskId = taskId;
        this.createdAt = createdAt.toInstant().toEpochMilli();
        this.type = type;
        this.priority = priority;
        this.status = status;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
