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
package org.wrkr.clb.notification.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification {

    @JsonProperty(value = Notification_.subscriberId)
    private long subscriberId;
    @JsonProperty(value = Notification_.timestamp)
    private long timestamp;
    @JsonProperty(value = Notification_.notificationType)
    private String notificationType;
    @JsonProperty(value = Notification_.json)
    private String json;

    public long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Notification() {
    }

    public Notification(long subscriberId, String notificationType, String json) {
        this.subscriberId = subscriberId;
        this.notificationType = notificationType;
        this.json = json;
    }
}
