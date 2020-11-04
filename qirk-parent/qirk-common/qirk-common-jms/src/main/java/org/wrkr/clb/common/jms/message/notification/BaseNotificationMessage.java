package org.wrkr.clb.common.jms.message.notification;

import java.util.Collection;

import org.wrkr.clb.common.util.strings.JSONifiable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseNotificationMessage implements JSONifiable {

    public static class Type {
        public static final String SERVICE_NEWS = "SERVICE_NEWS";

        public static final String TASK_CREATED = "TASK_CREATED";
        public static final String TASK_UPDATED = "TASK_UPDATED";
        public static final String TASK_COMMENTED = "TASK_COMMENTED";
    }

    public static final String TYPE = "type";
    public static final String SUBSCRIBER_IDS = "subscriber_ids";
    public static final String SUBSCRIBER_EMAILS = "subscriber_emails";

    @JsonProperty(value = TYPE)
    public String type;
    @JsonProperty(value = SUBSCRIBER_IDS)
    @JsonInclude(Include.NON_NULL)
    public Collection<Long> subscriberIds;
    @JsonProperty(value = SUBSCRIBER_EMAILS)
    @JsonInclude(Include.NON_NULL)
    public Collection<String> subscriberEmails;

    protected BaseNotificationMessage(String type) {
        this.type = type;
    }

    public BaseNotificationMessage(String type, Collection<Long> subscriberIds, Collection<String> subscriberEmails) {
        this.type = type;
        this.subscriberIds = subscriberIds;
        this.subscriberEmails = subscriberEmails;
    }

    @Override
    public String toString() {
        return getClass() + " [type=" + type + "]";
    }
}
