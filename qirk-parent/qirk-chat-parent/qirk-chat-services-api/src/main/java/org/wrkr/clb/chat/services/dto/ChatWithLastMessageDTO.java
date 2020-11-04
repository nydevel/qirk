package org.wrkr.clb.chat.services.dto;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.sql.DialogMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatWithLastMessageDTO extends ChatDTO {

    @JsonProperty(value = "last_message")
    public MessageDTO lastMessage;

    public ChatWithLastMessageDTO() {
    }

    public static ChatWithLastMessageDTO fromEntity(BaseAttachedChatMessage message) {
        ChatWithLastMessageDTO dto = new ChatWithLastMessageDTO();

        dto.chatType = message.getChatType();
        dto.chatId = message.getChatId();
        dto.lastMessage = MessageDTO.fromEntity(message);

        return dto;
    }

    public static ChatWithLastMessageDTO fromEntity(DialogMessage message) {
        ChatWithLastMessageDTO dto = new ChatWithLastMessageDTO();

        dto.chatType = message.getChatType();
        if (message.getSenderId() != message.getUser1Id()) {
            dto.chatId = message.getUser1Id();
        } else if (message.getSenderId() != message.getUser2Id()) {
            dto.chatId = message.getUser2Id();
        }
        dto.lastMessage = MessageDTO.fromEntity(message);

        return dto;
    }

    public static <M extends BaseAttachedChatMessage> List<ChatWithLastMessageDTO> fromAttachedChatMessages(
            List<M> messageList) {
        List<ChatWithLastMessageDTO> dtoList = new ArrayList<ChatWithLastMessageDTO>(messageList.size());
        for (M message : messageList) {
            dtoList.add(fromEntity(message));
        }
        return dtoList;
    }

    public static List<ChatWithLastMessageDTO> fromDialogMessages(List<DialogMessage> messageList) {
        List<ChatWithLastMessageDTO> dtoList = new ArrayList<ChatWithLastMessageDTO>(messageList.size());
        for (DialogMessage message : messageList) {
            dtoList.add(fromEntity(message));
        }
        return dtoList;
    }
}
