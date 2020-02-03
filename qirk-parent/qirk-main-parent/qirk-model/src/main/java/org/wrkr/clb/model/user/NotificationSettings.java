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
package org.wrkr.clb.model.user;

import org.wrkr.clb.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationSettings extends BaseEntity {

    public static enum Setting {
        TASK_CREATED("TASK_CREATED"),
        TASK_UPDATED("TASK_UPDATED"),
        TASK_COMMENTED("TASK_COMMENTED");

        @SuppressWarnings("unused")
        private final String nameCode;

        Setting(final String nameCode) {
            this.nameCode = nameCode;
        }
    }

    @JsonIgnore
    private long userId;
    @JsonProperty(value = "task_created")
    private boolean taskCreated = true;
    @JsonProperty(value = "task_updated")
    private boolean taskUpdated = true;
    @JsonProperty(value = "task_commented")
    private boolean taskCommented = true;

    public NotificationSettings() {
    }

    public NotificationSettings(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isTaskCreated() {
        return taskCreated;
    }

    public void setTaskCreated(boolean taskCreated) {
        this.taskCreated = taskCreated;
    }

    public boolean isTaskUpdated() {
        return taskUpdated;
    }

    public void setTaskUpdated(boolean taskUpdated) {
        this.taskUpdated = taskUpdated;
    }

    public boolean isTaskCommented() {
        return taskCommented;
    }

    public void setTaskCommented(boolean taskCommented) {
        this.taskCommented = taskCommented;
    }
}
