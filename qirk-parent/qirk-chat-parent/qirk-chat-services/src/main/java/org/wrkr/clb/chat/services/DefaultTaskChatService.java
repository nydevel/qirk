/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
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
