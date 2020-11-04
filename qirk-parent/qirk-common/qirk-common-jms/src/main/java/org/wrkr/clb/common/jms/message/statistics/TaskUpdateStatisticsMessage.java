package org.wrkr.clb.common.jms.message.statistics;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskUpdateStatisticsMessage extends BaseStatisticsMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(TaskUpdateStatisticsMessage.class);

    public static final String PROJECT_ID = "project_id";
    public static final String PROJECT_NAME = "project_name";
    public static final String TASK_ID = "task_id";
    public static final String UPDATED_AT = "updated_at";
    public static final String TYPE = "type";
    public static final String PRIORITY = "priority";
    public static final String STATUS = "status";

    @JsonProperty(value = PROJECT_ID)
    public long projectId;
    @JsonProperty(value = PROJECT_NAME)
    public String projectName;
    @JsonProperty(value = TASK_ID)
    public long taskId;
    @JsonProperty(value = UPDATED_AT)
    public long updatedAt;
    @JsonProperty(value = TYPE)
    public String type;
    @JsonProperty(value = PRIORITY)
    public String priority;
    @JsonProperty(value = STATUS)
    public String status;

    public TaskUpdateStatisticsMessage(long projectId, String projectName, long taskId, OffsetDateTime updatedAt) {
        super(Code.TASK_UPDATE);
        this.projectId = projectId;
        this.projectName = projectName;
        this.taskId = taskId;
        this.updatedAt = updatedAt.toInstant().toEpochMilli();
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
