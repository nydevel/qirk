package org.wrkr.clb.common.crypto.token.chat;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskChatTokenData extends ChatTokenData {

    private static final ObjectWriter TASK_CHAT_TOKEN_DATA_WRITER = new ObjectMapper().writerFor(TaskChatTokenData.class);

    public static final String SENDER_USERNAME = "sender_username";
    public static final String SENDER_FULL_NAME = "sender_full_name";

    public static final String PROJECT_ID = "proj_id";
    public static final String PROJECT_UI_ID = "proj_ui_id";
    public static final String PROJECT_NAME = "proj_name";
    public static final String TASK_NUMBER = "task_number";
    public static final String TASK_SUMMARY = "task_summary";

    @JsonProperty(value = SENDER_USERNAME)
    public String senderUsername;
    @JsonProperty(value = SENDER_FULL_NAME)
    public String senderFullName;

    @JsonProperty(value = PROJECT_ID)
    public long projectId;
    @JsonProperty(value = PROJECT_UI_ID)
    public String projectUiId;
    @JsonProperty(value = PROJECT_NAME)
    public String projectName;
    @JsonProperty(value = TASK_NUMBER)
    public long taskNumber;
    @JsonProperty(value = TASK_SUMMARY)
    public String taskSummary;

    public TaskChatTokenData() {
    }

    public TaskChatTokenData(Map<String, Object> map) {
        super(map);

        senderUsername = (String) map.get(SENDER_USERNAME);
        senderFullName = (String) map.get(SENDER_FULL_NAME);

        projectId = (Long) map.get(PROJECT_ID);
        projectUiId = (String) map.get(PROJECT_UI_ID);
        projectName = (String) map.get(PROJECT_NAME);
        taskNumber = (Long) map.get(TASK_NUMBER);
        taskSummary = (String) map.get(TASK_SUMMARY);
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return TASK_CHAT_TOKEN_DATA_WRITER.writeValueAsString(this);
    }
}
