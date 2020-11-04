package org.wrkr.clb.chat.services.sql;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.sql.TaskMessage;
import org.wrkr.clb.chat.repo.sql.BaseAttachedChatMessageRepo;
import org.wrkr.clb.chat.repo.sql.SQLTaskMessageRepo;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;


@Service
public class SQLTaskChatService extends SQLAttachedChatService {

    @Autowired
    private SQLTaskMessageRepo messageRepo;

    @Override
    protected BaseAttachedChatMessageRepo getRepo() {
        return messageRepo;
    }

    @Override
    protected BaseAttachedChatMessage createMessageInstance(ChatTokenData tokenData, String message, UUID uuid) {
        BaseAttachedChatMessage taskMessage = new TaskMessage();
        taskMessage.setUuid(uuid.toString());
        taskMessage.setChatId(tokenData.chatId);
        taskMessage.setMessage(message);
        if (tokenData.senderId != null) {
            taskMessage.setSenderId(tokenData.senderId);
        } else {
            taskMessage.setSenderId(BaseAttachedChatMessage.SENDER_TYPE_EXAMPLE);
        }
        return taskMessage;
    }
}
