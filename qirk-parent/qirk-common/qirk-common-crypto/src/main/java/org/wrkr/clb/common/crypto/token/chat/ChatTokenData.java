package org.wrkr.clb.common.crypto.token.chat;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ChatTokenData extends SecurityTokenData {

    private static final ObjectWriter CHAT_TOKEN_DATA_WRITER = new ObjectMapper().writerFor(ChatTokenData.class);

    public static final String CHAT_TYPE = "chat_type";
    public static final String CHAT_ID = "chat_id";

    @JsonProperty(value = CHAT_TYPE)
    public String chatType;
    @JsonProperty(value = CHAT_ID)
    public long chatId;

    public static ChatTokenData fromReadWriteTokenData(SecurityTokenData tokenData, String chatType, Long chatId) {
        if (tokenData == null) {
            return null;
        }

        ChatTokenData chatTokenData = (ChatTokenData) tokenData;
        chatTokenData.chatType = chatType;
        chatTokenData.chatId = chatId;
        return chatTokenData;
    }

    public ChatTokenData() {
    }

    public ChatTokenData(Map<String, Object> map) {
        chatType = (String) map.get(CHAT_TYPE);
        chatId = (Long) map.get(CHAT_ID);
        senderId = (Long) map.get(SENDER_ID);
        write = (Boolean) map.get(WRITE);
        notBefore = (Long) map.get(NOT_BEFORE);
        notOnOrAfter = (Long) map.get(NOT_ON_OR_AFTER);
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return CHAT_TOKEN_DATA_WRITER.writeValueAsString(this);
    }
}
