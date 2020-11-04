package org.wrkr.clb.model.auth;

import java.time.OffsetDateTime;

import org.wrkr.clb.model.BaseIdEntity;

public class RememberMeToken extends BaseIdEntity {

    private String token;

    private long userId;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
