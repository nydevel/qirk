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
package org.wrkr.clb.notification.repo.dto;

import java.util.Map;

import org.wrkr.clb.common.util.datetime.DateTimeWithEpochDTO;
import org.wrkr.clb.notification.model.Notification_;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NotificationDTO {

    private static final ObjectWriter NOTIFICATION_DTO_WRITER = new ObjectMapper().writerFor(NotificationDTO.class);

    public static final String SUBSCRIBER_ID = "subscriber_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String NOTIFICATION_TYPE = "notification_type";
    public static final String JSON = "json";

    @JsonProperty(value = SUBSCRIBER_ID)
    @JsonInclude(Include.NON_NULL)
    public Long subscriberId;
    @JsonProperty(value = TIMESTAMP)
    public DateTimeWithEpochDTO timestamp;
    @JsonProperty(value = NOTIFICATION_TYPE)
    public String notificationType;
    @JsonProperty(value = JSON)
    public String json;

    public static NotificationDTO fromRow(Map<String, Object> row) {
        return new NotificationDTO(
                (Long) row.get(Notification_.timestamp),
                (String) row.get(Notification_.notificationType),
                (String) row.get(Notification_.json));
    }

    public NotificationDTO(Long subscriberId, long timestamp, String notificationType, String json) {
        this.subscriberId = subscriberId;
        this.timestamp = new DateTimeWithEpochDTO(timestamp);
        this.notificationType = notificationType;
        this.json = json;
    }

    public NotificationDTO(long timestamp, String notificationType, String json) {
        this.timestamp = new DateTimeWithEpochDTO(timestamp);
        this.notificationType = notificationType;
        this.json = json;
    }

    public String toJson() throws JsonProcessingException {
        return NOTIFICATION_DTO_WRITER.writeValueAsString(this);
    }
}
