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
package org.wrkr.clb.chat.services.jms;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.chat.services.dto.ChatDTO;
import org.wrkr.clb.chat.services.jms.MQDestination;
import org.wrkr.clb.chat.services.util.json.JsonContainer_;
import org.wrkr.clb.common.util.strings.JsonUtils;

public abstract class ChatListener implements MessageListener {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_RETRIES_COUNT = 50;

    protected abstract Map<Long, ConcurrentHashMap<String, MQDestination>> getChatIdToSessionIdsToControllers();

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                String text = ((TextMessage) message).getText();

                @SuppressWarnings("unchecked")
                Map<String, Object> meta = (Map<String, Object>) JsonUtils
                        .<Object>convertJsonToMapUsingLongForInts(text).get(JsonContainer_.meta);
                String chatType = (String) (meta.get(ChatDTO.CHAT_TYPE));
                long chatId = (Long) (meta.get(ChatDTO.CHAT_ID));
                ConcurrentHashMap<String, MQDestination> sessionIdToController = getChatIdToSessionIdsToControllers()
                        .get(chatId);

                if (sessionIdToController != null) {
                    sessionIdToController.forEach((key, value) -> {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("sendMessageFromMQ::chatType=" + chatType + "; chatId=" + chatId + "; sessionId=" + key);
                        }
                        value.sendMessageFromMQ(chatType, chatId, text);
                    });
                }

            } catch (Exception e) {
                LOG.error("Exception caught at listener", e);
                throw new RuntimeException(e);
            }
        } else {
            LOG.error("JMS Message is not instance of TextMessage");
        }
    }

    private void logSubscribedChatIds(String method) {
        if (LOG.isTraceEnabled()) {
            getChatIdToSessionIdsToControllers().forEach((key, value) -> {
                LOG.trace(method + "::chatId= " + key + "; sessionIds= " +
                        value.keySet().toString());
            });
        }
    }

    public void addController(MQDestination controller, long chatId, String sessionId) {
        int retriesCount = DEFAULT_RETRIES_COUNT;
        do {
            ConcurrentHashMap<String, MQDestination> emptySessionIdToController = new ConcurrentHashMap<String, MQDestination>();
            ConcurrentHashMap<String, MQDestination> sessionIdToController = getChatIdToSessionIdsToControllers().putIfAbsent(
                    chatId, emptySessionIdToController);
            if (sessionIdToController == null) {
                sessionIdToController = emptySessionIdToController;
            }
            sessionIdToController.put(sessionId, controller);

            // validate that sessionIdToController is still inside
            // chatIdToSessionIdsToControllers
            ConcurrentHashMap<String, MQDestination> newSessionIdToController = getChatIdToSessionIdsToControllers()
                    .get(chatId);
            if (newSessionIdToController == sessionIdToController) {
                break;
            }
            retriesCount--;
        } while (retriesCount > 0);

        logSubscribedChatIds("addController");
    }

    public void removeControllerFromChat(long chatId, String sessionId) {
        ConcurrentHashMap<String, MQDestination> sessionIdToController = getChatIdToSessionIdsToControllers().get(chatId);
        if (sessionIdToController != null) {
            sessionIdToController.remove(sessionId);
            if (sessionIdToController.isEmpty()) {
                ConcurrentHashMap<String, MQDestination> emptyMap = new ConcurrentHashMap<String, MQDestination>(
                        sessionIdToController);
                if (emptyMap.isEmpty()) {
                    getChatIdToSessionIdsToControllers().remove(chatId, emptyMap);
                }
            }
        }

        logSubscribedChatIds("removeControllerFromChat");
    }

    public void removeController(Set<Long> chatIds, String sessionId) {
        for (Long chatId : chatIds) {
            removeControllerFromChat(chatId, sessionId);
        }
    }
}
