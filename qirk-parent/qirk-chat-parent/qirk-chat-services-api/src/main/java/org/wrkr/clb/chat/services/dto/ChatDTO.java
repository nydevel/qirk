package org.wrkr.clb.chat.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatDTO {

    @JsonIgnore
    public static final String CHAT_TYPE = "chat_type";
    @JsonIgnore
    public static final String CHAT_ID = "chat_id";

    @JsonProperty(CHAT_TYPE)
    public String chatType;

    @JsonProperty(CHAT_ID)
    public long chatId;

    public ChatDTO() {
    }

    public ChatDTO(String chatType, long chatId) {
        this.chatType = chatType;
        this.chatId = chatId;
    }
}
