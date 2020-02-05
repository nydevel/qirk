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
package org.wrkr.clb.chat.web.controller;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.wrkr.clb.chat.services.DialogChatService;
import org.wrkr.clb.chat.services.IssueChatService;
import org.wrkr.clb.chat.services.ProjectChatService;
import org.wrkr.clb.chat.services.TaskChatService;
import org.wrkr.clb.chat.services.dto.ChatDTO;
import org.wrkr.clb.chat.services.dto.ChatWithLastMessageDTO;
import org.wrkr.clb.chat.services.dto.ExternalUuidDTO;
import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.chat.services.jms.DialogChatListener;
import org.wrkr.clb.chat.services.jms.DialogChatSender;
import org.wrkr.clb.chat.services.jms.IssueChatListener;
import org.wrkr.clb.chat.services.jms.IssueChatSender;
import org.wrkr.clb.chat.services.jms.MQDestination;
import org.wrkr.clb.chat.services.jms.ProjectChatListener;
import org.wrkr.clb.chat.services.jms.ProjectChatSender;
import org.wrkr.clb.chat.services.jms.TaskChatListener;
import org.wrkr.clb.chat.services.jms.TaskChatSender;
import org.wrkr.clb.chat.services.security.SecurityService;
import org.wrkr.clb.chat.services.util.json.JsonStatusCode;
import org.wrkr.clb.chat.web.json.JsonContainer;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;
import org.wrkr.clb.common.crypto.token.chat.MultipleChatTokenData;
import org.wrkr.clb.common.crypto.token.chat.SecurityTokenData;
import org.wrkr.clb.common.crypto.token.chat.TaskChatTokenData;
import org.wrkr.clb.common.util.chat.ChatType;
import org.wrkr.clb.common.util.strings.JsonUtils;

import com.fasterxml.jackson.core.JsonProcessingException;


// comment in full version
@SuppressWarnings("unused")
@ServerEndpoint(value = "/chat")
public class ChatWebSocketControllerStub {

    private static final Logger LOG = LoggerFactory.getLogger(ChatWebSocketControllerStub.class);

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        session.setMaxIdleTimeout(16 * 60 * 1000); // 16 minutes
    }

    @OnMessage
    public void onMessage(Session session, String message) throws Exception {
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }

    private void logAndSendError(Session session, Throwable throwable) {
        LOG.error("onError::" + throwable.getMessage() + "; SessionId=" + session.getId(), throwable);
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(JsonContainer.fromErrorCode(JsonStatusCode.INTERNAL_SERVER_ERROR));
            } catch (Exception e) {
                LOG.error("onError::sendTextToSession::" + e.getMessage() + "; SessionId=" + session.getId(), e);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (throwable instanceof EOFException) {
            LOG.trace("Connection reset by peer");
        } else {
            logAndSendError(session, throwable);
        }
    }
}
