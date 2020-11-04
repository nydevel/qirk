package org.wrkr.clb.model.project.task;

import org.wrkr.clb.model.BaseEntity;

public class TaskSubscriber extends BaseEntity {

    private long taskId;
    private long userId;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
