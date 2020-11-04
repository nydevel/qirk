package org.wrkr.clb.chat.services.sql;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.sql.ProjectMessage;
import org.wrkr.clb.chat.repo.sql.BaseAttachedChatMessageRepo;
import org.wrkr.clb.chat.repo.sql.SQLProjectMessageRepo;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;


@Service
public class SQLProjectChatService extends SQLAttachedChatService {

    @Autowired
    private SQLProjectMessageRepo messageRepo;

    @Override
    protected BaseAttachedChatMessageRepo getRepo() {
        return messageRepo;
    }

    @Override
    protected BaseAttachedChatMessage createMessageInstance(ChatTokenData tokenData, String message, UUID uuid) {
        BaseAttachedChatMessage projectMessage = new ProjectMessage();
        projectMessage.setUuid(uuid.toString());
        projectMessage.setChatId(tokenData.chatId);
        projectMessage.setMessage(message);
        if (tokenData.senderId != null) {
            projectMessage.setSenderId(tokenData.senderId);
        } else {
            projectMessage.setSenderId(BaseAttachedChatMessage.SENDER_TYPE_EXAMPLE);
        }
        return projectMessage;
    }
}
