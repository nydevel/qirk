package org.wrkr.clb.model.user;

import org.wrkr.clb.model.BaseEntity;

public class FailedLoginAttempt extends BaseEntity {

    private long userId;

    private long failedAt;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(long failedAt) {
        this.failedAt = failedAt;
    }
}
