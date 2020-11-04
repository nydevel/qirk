package org.wrkr.clb.common.jms.message.statistics;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NewTaskMessage extends BaseStatisticsMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(NewTaskMessage.class);

    public static final String PROJECT_ID = "project_id";
    public static final String PROJECT_NAME = "project_name";
    public static final String TASK_ID = "task_id";
    public static final String CREATED_AT = "created_at";
    public static final String TYPE = "type";
    public static final String PRIORITY = "priority";
    public static final String STATUS = "status";

    @JsonProperty(value = PROJECT_ID)
    public long projectId;
    @JsonProperty(value = PROJECT_NAME)
    public String projectName;
    @JsonProperty(value = TASK_ID)
    public long taskId;
    @JsonProperty(value = CREATED_AT)
    public long createdAt;
    @JsonProperty(value = TYPE)
    public String type;
    @JsonProperty(value = PRIORITY)
    public String priority;
    @JsonProperty(value = STATUS)
    public String status;

    public NewTaskMessage(long projectId, String projectName, long taskId, OffsetDateTime createdAt,
            String type, String priority, String status) {
        super(Code.NEW_TASK);
        this.projectId = projectId;
        this.projectName = projectName;
        this.taskId = taskId;
        this.createdAt = createdAt.toInstant().toEpochMilli();
        this.type = type;
        this.priority = priority;
        this.status = status;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
