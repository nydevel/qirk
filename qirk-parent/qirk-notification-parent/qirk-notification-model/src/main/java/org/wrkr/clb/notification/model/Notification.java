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
