package org.wrkr.clb.common.jms.message.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskCommentMessage extends BaseTaskNotificationMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(TaskCommentMessage.class);

    public static final String SENDER_USERNAME = "sender_username";
    public static final String SENDER_FULL_NAME = "sender_full_name";

    public static final String PROJECT_ID = "project_id";
    public static final String PROJECT_NAME = "project_name";
    public static final String TASK_ID = "task_id";
    public static final String TASK_NUMBER = "task_number";
    public static final String TASK_SUMMARY = "task_summary";

    @JsonIgnore
    public static final String MESSAGE = "message";
    @JsonIgnore
    public static final String COMMENTED_AT = "commented_at";

    @JsonProperty(value = SENDER_USERNAME)
    public String senderUsername;
    @JsonProperty(value = SENDER_FULL_NAME)
    public String senderFullName;

    @JsonProperty(value = PROJECT_ID)
    public long projectId;
    @JsonProperty(value = PROJECT_NAME)
    public String projectName;
    @JsonProperty(value = TASK_ID)
    public long taskId;
    @JsonProperty(value = TASK_NUMBER)
    public long taskNumber;
    @JsonProperty(value = TASK_SUMMARY)
    public String taskSummary;

    @JsonProperty(value = MESSAGE)
    public String message;
    @JsonProperty(value = COMMENTED_AT)
    public String commentedAt;

    public TaskCommentMessage() {
        super(BaseNotificationMessage.Type.TASK_COMMENTED);
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
