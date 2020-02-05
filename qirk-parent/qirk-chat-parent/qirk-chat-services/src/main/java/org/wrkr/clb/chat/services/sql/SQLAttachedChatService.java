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
package org.wrkr.clb.chat.services.sql;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.repo.sql.BaseAttachedChatMessageRepo;
import org.wrkr.clb.chat.services.dto.ChatWithLastMessageDTO;
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

    @Deprecated
    @Override
    @Transactional(value = "dsTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<ChatWithLastMessageDTO> getChatList(List<Long> chatIds) {
        List<BaseAttachedChatMessage> messageList = getRepo().getChatList(chatIds);
        return ChatWithLastMessageDTO.fromAttachedChatMessages(messageList);
    }
}
