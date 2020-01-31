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
package org.wrkr.clb.chat.services.cassandra;

import java.util.List;
import java.util.UUID;

import org.wrkr.clb.chat.model.cassandra.BaseChatMessage;
import org.wrkr.clb.chat.repo.cassandra.BaseChatMessageRepo;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;

public abstract class CassandraChatService {

    protected static final int LIST_BY_CHAT_ID_LIMIT = 20;
    protected static final int DEFAULT_RETRIES_COUNT = 3;

    protected abstract BaseChatMessageRepo getRepo();

    protected abstract BaseChatMessage createMessageInstance(
            ChatTokenData tokenData, String message, UUID uuid, long timestamp);

    public abstract List<MessageDTO> getLastMessages(ChatTokenData tokenData, long timestamp);

    public BaseChatMessage saveMessage(ChatTokenData tokenData, String message, UUID uuid, long timestamp) {
        BaseChatMessage messageInstance = createMessageInstance(tokenData, message, uuid, timestamp);
        if (!getRepo().save(messageInstance)) {
            throw new RuntimeException("Saving message in cassandra failed");
        }
        return messageInstance;
    }

    public BaseChatMessage saveMessage(ChatTokenData tokenData, String message, UUID uuid) {
        BaseChatMessage messageInstance = createMessageInstance(tokenData, message, uuid, System.currentTimeMillis());

        int retriesCount = DEFAULT_RETRIES_COUNT;
        do {
            if (getRepo().save(messageInstance)) {
                return messageInstance;
            } else {
                messageInstance.setTimestamp(System.currentTimeMillis());
                retriesCount--;
            }
        } while (retriesCount > 0);
        throw new RuntimeException("Saving message in cassandra failed");
    }
}
