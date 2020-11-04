package org.wrkr.clb.chat.services;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.chat.services.sql.SQLChatService;
import org.wrkr.clb.chat.services.sql.SQLTaskChatService;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;
import org.wrkr.clb.common.crypto.token.chat.TaskChatTokenData;
import org.wrkr.clb.common.jms.message.notification.TaskCommentMessage;
import org.wrkr.clb.common.jms.services.TaskLookupSubscribersSender;

@Component
public class DefaultTaskChatService extends DefaultChatService implements TaskChatService {

    private static final int MESSAGE_PREVIEW_LENGTH = 80;

    @Deprecated
    @Autowired(required = false)
    private SQLTaskChatService mariaDBService;

    @Autowired
    private TaskLookupSubscribersSender lookupSubscribersSender;

    @Deprecated
    @Override
    protected SQLChatService getSQLService() {
        return mariaDBService;
    }

    private TaskCommentMessage buildNotificationMessage(TaskChatTokenData tokenData, String message, String commentedAt) {
        TaskCommentMessage notificationMessage = new TaskCommentMessage();

        notificationMessage.senderUsername = tokenData.senderUsername;
        notificationMessage.senderFullName = tokenData.senderFullName;

        notificationMessage.projectId = tokenData.projectId;
        notificationMessage.projectUiId = tokenData.projectUiId;
        notificationMessage.projectName = tokenData.projectName;
        notificationMessage.taskId = tokenData.chatId;
        notificationMessage.taskNumber = tokenData.taskNumber;
        notificationMessage.taskSummary = tokenData.taskSummary;

        notificationMessage.message = (message.length() <= MESSAGE_PREVIEW_LENGTH
                ? message
                : (message.substring(0, MESSAGE_PREVIEW_LENGTH - 1) + "…"));
        notificationMessage.commentedAt = commentedAt;

        return notificationMessage;
    }

    @Override
    public MessageDTO saveMessage(ChatTokenData tokenData, String message) throws SQLException {
        MessageDTO dto = super.saveMessage(tokenData, message);
        TaskCommentMessage notificationMessage = buildNotificationMessage((TaskChatTokenData) tokenData, dto.message,
                dto.timestamp.iso8601);
        lookupSubscribersSender.send(notificationMessage);
        return dto;
    }
}
