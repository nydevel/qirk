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
