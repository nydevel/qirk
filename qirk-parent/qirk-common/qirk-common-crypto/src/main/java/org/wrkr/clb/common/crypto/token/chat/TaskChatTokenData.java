/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.common.crypto.token.chat;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskChatTokenData extends ChatTokenData {

    private static final ObjectWriter TASK_CHAT_TOKEN_DATA_WRITER = new ObjectMapper().writerFor(TaskChatTokenData.class);

    @JsonIgnore
    public static final String SENDER_USERNAME = "sender_username";
    @JsonIgnore
    public static final String SENDER_FULL_NAME = "sender_full_name";

    @JsonIgnore
    public static final String ORGANIZATION_ID = "org_id";
    @JsonIgnore
    public static final String ORGANIZATION_UI_ID = "org_ui_id";
    @JsonIgnore
    public static final String PROJECT_ID = "proj_id";
    @JsonIgnore
    public static final String PROJECT_UI_ID = "proj_ui_id";
    @JsonIgnore
    public static final String PROJECT_NAME = "proj_name";
    @JsonIgnore
    public static final String TASK_NUMBER = "task_number";
    @JsonIgnore
    public static final String TASK_SUMMARY = "task_summary";

    @JsonProperty(value = SENDER_USERNAME)
    public String senderUsername;
    @JsonProperty(value = SENDER_FULL_NAME)
    public String senderFullName;

    @JsonProperty(value = ORGANIZATION_ID)
    public long organizationId;
    @JsonProperty(value = ORGANIZATION_UI_ID)
    public String organizationUiId;
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

        organizationId = (Long) map.get(ORGANIZATION_ID);
        organizationUiId = (String) map.get(ORGANIZATION_UI_ID);
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
