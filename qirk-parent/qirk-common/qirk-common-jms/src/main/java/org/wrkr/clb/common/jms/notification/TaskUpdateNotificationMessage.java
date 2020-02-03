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

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskUpdateNotificationMessage extends TaskMessage {

    private static final ObjectWriter TASK_UPDATE_MESSAGE_WRITER = new ObjectMapper()
            .writerFor(TaskUpdateNotificationMessage.class);

    @JsonIgnore
    public static final String OLD_ASSIGNEE = "old_assignee";
    @JsonIgnore
    public static final String OLD_TYPE = "old_type";
    @JsonIgnore
    public static final String OLD_PRIORITY = "old_priority";
    @JsonIgnore
    public static final String OLD_STATUS = "old_status";

    @JsonProperty(value = OLD_ASSIGNEE)
    public Long oldAssignee;
    @JsonProperty(value = OLD_TYPE)
    public String oldType;
    @JsonProperty(value = OLD_PRIORITY)
    public String oldPriority;
    @JsonProperty(value = OLD_STATUS)
    public String oldStatus;

    public TaskUpdateNotificationMessage() {
        super(BaseNotificationMessage.Type.TASK_UPDATED);
    }

    public TaskUpdateNotificationMessage(Collection<Long> subscriberIds, Collection<String> subscriberEmails) {
        super(BaseNotificationMessage.Type.TASK_UPDATED, subscriberIds, subscriberEmails);
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return TASK_UPDATE_MESSAGE_WRITER.writeValueAsString(this);
    }
}
