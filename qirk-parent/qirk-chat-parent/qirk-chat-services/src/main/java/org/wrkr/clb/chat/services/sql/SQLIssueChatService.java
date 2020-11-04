package org.wrkr.clb.chat.services.sql;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.sql.IssueMessage;
import org.wrkr.clb.chat.repo.sql.BaseAttachedChatMessageRepo;
import org.wrkr.clb.chat.repo.sql.SQLIssueMessageRepo;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;


@Service
public class SQLIssueChatService extends SQLAttachedChatService {

    @Autowired
    private SQLIssueMessageRepo messageRepo;

    @Override
    protected BaseAttachedChatMessageRepo getRepo() {
        return messageRepo;
    }

    @Override
    protected BaseAttachedChatMessage createMessageInstance(ChatTokenData tokenData, String message, UUID uuid) {
        BaseAttachedChatMessage issueMessage = new IssueMessage();
        issueMessage.setUuid(uuid.toString());
        issueMessage.setChatId(tokenData.chatId);
        issueMessage.setMessage(message);
        if (tokenData.senderId != null) {
            issueMessage.setSenderId(tokenData.senderId);
        } else {
            issueMessage.setSenderId(BaseAttachedChatMessage.SENDER_TYPE_EXAMPLE);
        }
        return issueMessage;
    }
}
