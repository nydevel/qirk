/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.chat.services.mariadb;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.chat.model.mariadb.BaseChatMessage;
import org.wrkr.clb.chat.model.mariadb.DialogMessage;
import org.wrkr.clb.chat.repo.mariadb.MariaDBDialogMessageRepo;
import org.wrkr.clb.chat.services.dto.ChatWithLastMessageDTO;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;

@Service
public class MariaDBDialogChatService extends MariaDBChatService {

    @Autowired
    private MariaDBDialogMessageRepo messageRepo;

    @Override
    protected MariaDBDialogMessageRepo getRepo() {
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

    @Deprecated
    @Override
    @Transactional(value = "dsTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<ChatWithLastMessageDTO> getChatList(List<Long> chatIds) {
        List<DialogMessage> messageList = getRepo().getChatList(chatIds);
        return ChatWithLastMessageDTO.fromDialogMessages(messageList);
    }
}
