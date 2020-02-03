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
package org.wrkr.clb.chat.services.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.repo.sql.BaseChatMessageRepo;
import org.wrkr.clb.chat.services.dto.ChatWithLastMessageDTO;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;

public abstract class SQLChatService {

    protected static final int LIST_BY_CHAT_ID_LIMIT = 20;
    protected static final int DEFAULT_RETRIES_COUNT = 3;

    protected abstract BaseChatMessageRepo getRepo();

    protected abstract BaseChatMessage createMessageInstance(ChatTokenData tokenData, String message, UUID uuid);

    // @Transactional(value = "dsTransactionManager", readOnly = true)
    public abstract List<MessageDTO> getLastMessages(ChatTokenData tokenData, long timestamp);

    @Transactional(value = "dsTransactionManager", rollbackFor = Throwable.class)
    public BaseChatMessage saveMessage(ChatTokenData tokenData, String message, UUID uuid) throws SQLException {
        BaseChatMessage messageInstance = createMessageInstance(tokenData, message, uuid);

        int retriesCount = DEFAULT_RETRIES_COUNT;
        DataIntegrityViolationException caughtException = null;
        do {
            try {
                messageInstance.setTimestamp(System.currentTimeMillis());
                getRepo().save(messageInstance);
                return messageInstance;
            } catch (DataIntegrityViolationException e) {
                caughtException = e;
                retriesCount--;
            }
        } while (retriesCount > 0);
        throw caughtException;
    }

    // @Transactional(value = "dsTransactionManager", rollbackFor = Throwable.class)
    public abstract void delete(ChatTokenData tokenData, long timestamp);

    public abstract List<ChatWithLastMessageDTO> getChatList(List<Long> chatIds);
}
