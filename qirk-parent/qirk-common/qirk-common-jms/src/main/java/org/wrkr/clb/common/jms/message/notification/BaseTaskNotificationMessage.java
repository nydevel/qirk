package org.wrkr.clb.common.jms.message.notification;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseTaskNotificationMessage extends BaseNotificationMessage {

    public static final String PROJECT_UI_ID = "project_ui_id";

    @JsonProperty(value = PROJECT_UI_ID)
    public String projectUiId;

    protected BaseTaskNotificationMessage(String type) {
        super(type);
    }

    public BaseTaskNotificationMessage(String type, Collection<Long> subscriberIds, Collection<String> subscriberEmails) {
        super(type, subscriberIds, subscriberEmails);
    }
}
