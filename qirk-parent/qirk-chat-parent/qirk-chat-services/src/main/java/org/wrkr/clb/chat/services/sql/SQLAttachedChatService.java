package org.wrkr.clb.chat.services.sql;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.repo.sql.BaseAttachedChatMessageRepo;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;

public abstract class SQLAttachedChatService extends SQLChatService {

    @Override
    protected abstract BaseAttachedChatMessageRepo getRepo();

    @Override
    @Transactional(value = "dsTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<MessageDTO> getLastMessages(ChatTokenData tokenData, long timestamp) {
        long chatId = tokenData.chatId;
        List<BaseChatMessage> messageList = getRepo().listTopSinceTimestampByChatId(
                chatId, timestamp, LIST_BY_CHAT_ID_LIMIT);
        return MessageDTO.fromMariaDBEntities(messageList);
    }

    @Override
    @Transactional(value = "dsTransactionManager", rollbackFor = Throwable.class)
    public void delete(ChatTokenData tokenData, long timestamp) {
        long chatId = tokenData.chatId;
        getRepo().deleteByChatIdAndTimestamp(chatId, timestamp);
    }
}
