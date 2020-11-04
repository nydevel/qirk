package org.wrkr.clb.common.jms.message.notification;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskUpdateNotificationMessage extends TaskMessage {

    private static final ObjectWriter TASK_UPDATE_MESSAGE_WRITER = new ObjectMapper()
            .writerFor(TaskUpdateNotificationMessage.class);

    public static final String OLD_ASSIGNEE = "old_assignee";
    public static final String OLD_TYPE = "old_type";
    public static final String OLD_PRIORITY = "old_priority";
    public static final String OLD_STATUS = "old_status";

    @JsonProperty(value = OLD_ASSIGNEE)
    public Long oldAssignee;
    @JsonProperty(value = OLD_TYPE)
    public String oldType;
    @JsonProperty(value = OLD_PRIORITY)
    public String oldPriority;
    @JsonProperty(value = OLD_STATUS)
    public String oldStatus;

    public TaskUpdateNotificationMessage() {
        super(BaseNotificationMessage.Type.TASK_UPDATED);
    }

    public TaskUpdateNotificationMessage(Collection<Long> subscriberIds, Collection<String> subscriberEmails) {
        super(BaseNotificationMessage.Type.TASK_UPDATED, subscriberIds, subscriberEmails);
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return TASK_UPDATE_MESSAGE_WRITER.writeValueAsString(this);
    }
}
