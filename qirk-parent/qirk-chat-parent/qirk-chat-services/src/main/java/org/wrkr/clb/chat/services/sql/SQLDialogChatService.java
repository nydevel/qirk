package org.wrkr.clb.chat.services.sql;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.model.sql.DialogMessage;
import org.wrkr.clb.chat.repo.sql.SQLDialogMessageRepo;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;

@Service
public class SQLDialogChatService extends SQLChatService {

    @Autowired
    private SQLDialogMessageRepo messageRepo;

    @Override
    protected SQLDialogMessageRepo getRepo() {
        return messageRepo;
    }

    @Override
    protected DialogMessage createMessageInstance(ChatTokenData tokenData, String message, UUID uuid) {
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.setUuid(uuid.toString());
        dialogMessage.setUser1Id(Long.min(tokenData.senderId, tokenData.chatId));
        dialogMessage.setUser2Id(Long.max(tokenData.senderId, tokenData.chatId));
        dialogMessage.setMessage(message);
        dialogMessage.setSenderId(tokenData.senderId);
        return dialogMessage;
    }

    @Override
    @Transactional(value = "dsTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<MessageDTO> getLastMessages(ChatTokenData tokenData, long timestamp) {
        long user1Id = Long.min(tokenData.senderId, tokenData.chatId);
        long user2Id = Long.max(tokenData.senderId, tokenData.chatId);
        List<BaseChatMessage> messageList = getRepo().listTopSinceTimestampByUserId(
                user1Id, user2Id, timestamp, LIST_BY_CHAT_ID_LIMIT);
        return MessageDTO.fromMariaDBEntities(messageList);
    }

    @Override
    @Transactional(value = "dsTransactionManager", rollbackFor = Throwable.class)
    public void delete(ChatTokenData tokenData, long timestamp) {
        long user1Id = Long.min(tokenData.senderId, tokenData.chatId);
        long user2Id = Long.max(tokenData.senderId, tokenData.chatId);
        getRepo().deleteByUserIdAndTimestamp(user1Id, user2Id, timestamp);
    }
}
