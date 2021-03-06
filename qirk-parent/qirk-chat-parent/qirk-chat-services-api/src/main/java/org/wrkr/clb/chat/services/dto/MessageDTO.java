package org.wrkr.clb.chat.services.dto;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.model.sql.DialogMessage;
import org.wrkr.clb.common.util.datetime.DateTimeWithEpochDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDTO {

    @JsonIgnore
    public static final String CHAT_TYPE = "chat_type";
    @JsonIgnore
    public static final String CHAT_ID = "chat_id";

    @JsonProperty(value = "sender_id")
    public Long senderId;

    public DateTimeWithEpochDTO timestamp;

    public String message;

    @JsonProperty(CHAT_TYPE)
    @JsonInclude(Include.NON_NULL)
    public String chatType;

    @JsonProperty(CHAT_ID)
    @JsonInclude(Include.NON_NULL)
    public Long chatId;

    @JsonProperty(value = "external_uuid")
    @JsonInclude(Include.NON_NULL)
    public String externalUuid;

    public static MessageDTO fromEntity(BaseChatMessage message) {
        MessageDTO dto = new MessageDTO();

        dto.senderId = message.getSenderId();
        dto.timestamp = new DateTimeWithEpochDTO(message.getTimestamp());
        dto.message = message.getMessage();

        return dto;
    }

    public static MessageDTO fromEntityWithChat(BaseAttachedChatMessage message) {
        MessageDTO dto = fromEntity(message);
        dto.chatType = message.getChatType();
        dto.chatId = message.getChatId();
        return dto;
    }

    public static MessageDTO fromEntityWithChat(DialogMessage message) {
        MessageDTO dto = fromEntity(message);
        dto.chatType = message.getChatType();
        if (message.getSenderId() != message.getUser1Id()) {
            dto.chatId = message.getUser1Id();
        } else if (message.getSenderId() != message.getUser2Id()) {
            dto.chatId = message.getUser2Id();
        }
        return dto;
    }

    public static <M extends BaseChatMessage> List<MessageDTO> fromMariaDBEntities(
            List<M> messageList) {
        List<MessageDTO> dtoList = new ArrayList<MessageDTO>(messageList.size());
        for (org.wrkr.clb.chat.model.sql.BaseChatMessage message : messageList) {
            dtoList.add(fromEntity(message));
        }
        return dtoList;
    }
}
