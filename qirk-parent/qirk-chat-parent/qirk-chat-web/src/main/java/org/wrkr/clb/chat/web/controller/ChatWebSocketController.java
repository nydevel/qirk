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


// uncomment in full version
// @ServerEndpoint(value = "/chat") uncomment in full version
public class ChatWebSocketController implements MQDestination {

    private static final Logger LOG = LoggerFactory.getLogger(ChatWebSocketController.class);

    public static enum RequestType {
        PING("PING"),
        GET_CHAT_LIST("GET_CHAT_LIST"),
        GET_HISTORY("GET_HISTORY"),
        SEND_MESSAGE("SEND_MESSAGE"),
        REFRESH_TOKEN("REFRESH_TOKEN"),
        UNSUBSCRIBE("UNSUBSCRIBE");

        @SuppressWarnings("unused")
        private final String type;

        RequestType(final String type) {
            this.type = type;
        }
    }

    public static enum ResponseType {
        REFRESH_TOKEN("REFRESH_TOKEN"),
        CHAT_LIST("CHAT_LIST"),
        MESSAGES("MESSAGES"),
        MESSAGE_ACCEPTED("MESSAGE_ACCEPTED"),
        UNSUBSCRIBED("UNSUBSCRIBED");

        @SuppressWarnings("unused")
        private final String type;

        ResponseType(final String type) {
            this.type = type;
        }
    }

    private Session session;

    private SecurityService securityService;

    private TaskChatService taskChatService;
    private Map<Long, SecurityTokenData> taskChatIdToToken = new ConcurrentHashMap<Long, SecurityTokenData>();
    private TaskChatSender taskChatSender;
    private TaskChatListener taskChatListener;
    private Map<Long, ConcurrentLinkedQueue<String>> taskChatIdToMessageQueue = new ConcurrentHashMap<Long, ConcurrentLinkedQueue<String>>();

    private IssueChatService issueChatService;
    private Map<Long, SecurityTokenData> issueChatIdToToken = new ConcurrentHashMap<Long, SecurityTokenData>();
    private IssueChatSender issueChatSender;
    private IssueChatListener issueChatListener;
    private Map<Long, ConcurrentLinkedQueue<String>> issueChatIdToMessageQueue = new ConcurrentHashMap<Long, ConcurrentLinkedQueue<String>>();

    private ProjectChatService projectChatService;
    private Map<Long, SecurityTokenData> projectChatIdToToken = new ConcurrentHashMap<Long, SecurityTokenData>();
    private ProjectChatSender projectChatSender;
    private ProjectChatListener projectChatListener;
    private Map<Long, ConcurrentLinkedQueue<String>> projectChatIdToMessageQueue = new ConcurrentHashMap<Long, ConcurrentLinkedQueue<String>>();

    private DialogChatService dialogChatService;
    private Map<Long, SecurityTokenData> dialogChatIdToToken = new ConcurrentHashMap<Long, SecurityTokenData>();
    private DialogChatSender dialogChatSender;
    private DialogChatListener dialogChatListener;
    private Map<Long, ConcurrentLinkedQueue<String>> dialogChatIdToMessageQueue = new ConcurrentHashMap<Long, ConcurrentLinkedQueue<String>>();

    private static String convertMessagesToJson(List<MessageDTO> dtoList, String chatType, long chatId)
            throws JsonProcessingException, IOException {
        return JsonContainer.fromObjectsAndMeta(dtoList, new ChatDTO(chatType, chatId), ResponseType.MESSAGES);
    }

    @OnOpen
    public void onOpen(Session session, @SuppressWarnings("unused") EndpointConfig config) {
        LOG.debug("onOpen::" + session.getId());
        session.setMaxIdleTimeout(16 * 60 * 1000); // 16 minutes
        this.session = session;

        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        securityService = ctx.getBean(SecurityService.class);

        taskChatService = ctx.getBean(TaskChatService.class);
        taskChatSender = ctx.getBean(TaskChatSender.class);
        taskChatListener = ctx.getBean(TaskChatListener.class);

        issueChatService = ctx.getBean(IssueChatService.class);
        issueChatSender = ctx.getBean(IssueChatSender.class);
        issueChatListener = ctx.getBean(IssueChatListener.class);

        projectChatService = ctx.getBean(ProjectChatService.class);
        projectChatSender = ctx.getBean(ProjectChatSender.class);
        projectChatListener = ctx.getBean(ProjectChatListener.class);

        dialogChatService = ctx.getBean(DialogChatService.class);
        dialogChatSender = ctx.getBean(DialogChatSender.class);
        dialogChatListener = ctx.getBean(DialogChatListener.class);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws Exception {
        LOG.debug("onMessage::From=" + session.getId());

        Map<String, Object> data = JsonUtils.<Object>convertJsonToMapUsingLongForInts(message);
        RequestType requestType = null;
        try {
            requestType = RequestType.valueOf((String) data.get("request_type"));
        } catch (IllegalArgumentException e) {
            session.getBasicRemote().sendText(JsonContainer.fromErrorCode(JsonStatusCode.INVALID_REQUEST_TYPE));
            return;
        }

        try {
            ChatTokenData tokenData;
            String chatType = (String) data.get("chat_type");
            Long chatId = (Long) data.get("chat_id");
            String token = (String) data.get("token");
            String IV = (String) data.get("IV");

            switch (requestType) {
                case PING:
                    return;

                case GET_CHAT_LIST:
                    /*@formatter:off
                    MultipleChatTokenData multipleChatTokenData = new MultipleChatTokenData(
                            securityService.decryptToken(token, IV));
                    securityService.validateTokenDataOrThrowSecurityException(multipleChatTokenData, false);
                    getChatList(multipleChatTokenData);
                    for (String tokenChatType : multipleChatTokenData.chatTypeToChatIds.keySet()) {
                        List<Long> tokenChatIds = multipleChatTokenData.chatTypeToChatIds.get(tokenChatType);
                        for (Long tokenChatId : tokenChatIds) {
                            saveToken(session, tokenChatType, tokenChatId, multipleChatTokenData);
                            subscribe(session, tokenChatType, tokenChatId);
                        }
                    }
                    @formatter:on*/
                    break;

                case GET_HISTORY:
                    tokenData = getSavedTokenDataOrValidateAndSaveToken(session, chatType, chatId, token, IV, false);
                    subscribe(session, tokenData);
                    getHistory(session, tokenData, (Long) data.get("timestamp"));
                    break;

                case SEND_MESSAGE:
                    tokenData = getSavedTokenDataOrValidateAndSaveToken(session, chatType, chatId, token, IV, true);
                    MessageDTO dto = saveMessage(tokenData, (String) data.get("message"));
                    String externalUuid = (String) data.get("external_uuid");
                    session.getBasicRemote().sendText(
                            JsonContainer.fromObject(new ExternalUuidDTO(externalUuid), ResponseType.MESSAGE_ACCEPTED));
                    if (dto != null) {
                        dto.externalUuid = externalUuid;
                        sendMessageToMQ(tokenData, dto);
                    } else {
                        session.getBasicRemote()
                                .sendText(JsonContainer.fromErrorCode(JsonStatusCode.INTERNAL_SERVER_ERROR));
                    }
                    break;

                case REFRESH_TOKEN:
                    tokenData = saveToken(session, token, IV, false);
                    if (tokenData.write) {
                        flushMessageQueue(session, tokenData.chatType, tokenData.chatId);
                    }
                    break;

                case UNSUBSCRIBE:
                    tokenData = getSavedTokenData(session, chatType, chatId);
                    if (tokenData != null) {
                        unsubscribe(session, tokenData);
                    }
                    break;

                default:
                    return;
            }
        } catch (SecurityException e) {
            LOG.debug("onMessage::SecurityException::" + e.getMessage() + "; SessionId=" + session.getId(), e);
            session.getBasicRemote().sendText(JsonContainer.fromErrorCode(e.getMessage(), requestType));
        } catch (Exception e) {
            logAndSendError(session, e);
        }
    }

    private ChatTokenData getSavedTokenData(Session session, String chatType, Long chatId)
            throws JsonProcessingException, IOException {
        SecurityTokenData tokenData = null;
        if (chatType != null && chatId != null) {
            switch (chatType) {
                case ChatType.TASK:
                    tokenData = taskChatIdToToken.get(chatId);
                    break;

                case ChatType.ISSUE:
                    tokenData = issueChatIdToToken.get(chatId);
                    break;

                case ChatType.PROJECT:
                    tokenData = projectChatIdToToken.get(chatId);
                    break;

                case ChatType.DIALOG:
                    tokenData = dialogChatIdToToken.get(chatId);
                    break;

                default:
                    session.getBasicRemote().sendText(JsonContainer.fromErrorCode(JsonStatusCode.INVALID_CHAT_TYPE));
                    break;
            }
        }
        return ChatTokenData.fromReadWriteTokenData(tokenData, chatType, chatId);
    }

    private ChatTokenData getNewToken(String token, String IV) {
        Map<String, Object> map = securityService.decryptToken(token, IV);
        if ((Boolean) map.get(ChatTokenData.WRITE) && ChatType.TASK.equals((String) map.get(ChatTokenData.CHAT_TYPE))) {
            return new TaskChatTokenData(map);
        }
        return new ChatTokenData(map);
    }

    private SecurityTokenData saveToken(Session session, String chatType, Long chatId, SecurityTokenData tokenData)
            throws JsonProcessingException, IOException {
        switch (chatType) {
            case ChatType.TASK:
                taskChatIdToToken.put(chatId, tokenData);
                break;

            case ChatType.ISSUE:
                issueChatIdToToken.put(chatId, tokenData);
                break;

            case ChatType.PROJECT:
                projectChatIdToToken.put(chatId, tokenData);
                break;

            case ChatType.DIALOG:
                dialogChatIdToToken.put(chatId, tokenData);
                break;

            default:
                session.getBasicRemote().sendText(JsonContainer.fromErrorCode(JsonStatusCode.INVALID_TOKEN_CHAT_TYPE));
                break;
        }
        return tokenData;
    }

    private ChatTokenData saveToken(Session session, String token, String IV,
            boolean validateForWrite) throws JsonProcessingException, IOException {
        ChatTokenData tokenData = getNewToken(token, IV);
        securityService.validateTokenDataOrThrowSecurityException(tokenData, validateForWrite);
        saveToken(session, tokenData.chatType, tokenData.chatId, tokenData);
        return tokenData;
    }

    private ChatTokenData getSavedTokenDataOrValidateAndSaveToken(Session session, String chatType, Long chatId,
            String token, String IV, boolean validateForWrite) throws JsonProcessingException, IOException {
        ChatTokenData tokenData = getSavedTokenData(session, chatType, chatId);
        boolean valid = securityService.validateTokenData(tokenData, validateForWrite);
        if (!valid) {
            tokenData = saveToken(session, token, IV, validateForWrite);
        }
        return tokenData;
    }

    private void subscribe(Session session, String chatType, Long chatId)
            throws Exception {
        switch (chatType) {
            case ChatType.TASK:
                taskChatListener.addController(this, chatId, session.getId());
                break;

            case ChatType.ISSUE:
                issueChatListener.addController(this, chatId, session.getId());
                break;

            case ChatType.PROJECT:
                projectChatListener.addController(this, chatId, session.getId());
                break;

            case ChatType.DIALOG:
                dialogChatListener.addController(this, chatId, session.getId());
                break;
        }
    }

    private void subscribe(Session session, ChatTokenData tokenData) throws Exception {
        subscribe(session, tokenData.chatType, tokenData.chatId);
    }

    private void unsubscribe(Session session, ChatTokenData tokenData)
            throws Exception {
        Long chatId = tokenData.chatId;
        switch (tokenData.chatType) {
            case ChatType.TASK:
                taskChatListener.removeControllerFromChat(chatId, session.getId());
                taskChatIdToToken.remove(chatId);
                break;

            case ChatType.ISSUE:
                issueChatListener.removeControllerFromChat(chatId, session.getId());
                issueChatIdToToken.remove(chatId);
                break;

            case ChatType.PROJECT:
                projectChatListener.removeControllerFromChat(chatId, session.getId());
                projectChatIdToToken.remove(chatId);
                break;

            case ChatType.DIALOG:
                dialogChatListener.removeControllerFromChat(chatId, session.getId());
                dialogChatIdToToken.remove(chatId);
                break;
        }
        session.getBasicRemote().sendText(JsonContainer.fromObject(null, ResponseType.UNSUBSCRIBED));
    }

    @SuppressWarnings("unused")
    private void getChatList(MultipleChatTokenData multipleChatTokenData) throws JsonProcessingException, IOException {
        List<ChatWithLastMessageDTO> dtoList = new ArrayList<ChatWithLastMessageDTO>();
        dtoList.addAll(taskChatService.getChatList(multipleChatTokenData.chatTypeToChatIds.get(ChatType.TASK)));
        dtoList.addAll(issueChatService.getChatList(multipleChatTokenData.chatTypeToChatIds.get(ChatType.ISSUE)));
        dtoList.addAll(projectChatService.getChatList(multipleChatTokenData.chatTypeToChatIds.get(ChatType.PROJECT)));
        dtoList.addAll(dialogChatService.getChatList(multipleChatTokenData.chatTypeToChatIds.get(ChatType.DIALOG)));
        session.getBasicRemote().sendText(JsonContainer.fromObjects(dtoList, ResponseType.CHAT_LIST));
    }

    private void getHistory(Session session, ChatTokenData tokenData, Long timestamp) throws Exception {
        List<MessageDTO> dtoList = new ArrayList<MessageDTO>();
        switch (tokenData.chatType) {
            case ChatType.TASK:
                dtoList = taskChatService.getLastMessages(tokenData, timestamp);
                break;

            case ChatType.ISSUE:
                dtoList = issueChatService.getLastMessages(tokenData, timestamp);
                break;

            case ChatType.PROJECT:
                dtoList = projectChatService.getLastMessages(tokenData, timestamp);
                break;

            case ChatType.DIALOG:
                dtoList = dialogChatService.getLastMessages(tokenData, timestamp);
                break;
        }
        session.getBasicRemote().sendText(convertMessagesToJson(dtoList, tokenData.chatType, tokenData.chatId));
    }

    private MessageDTO saveMessage(ChatTokenData tokenData, String message) throws Exception {
        switch (tokenData.chatType) {
            case ChatType.TASK:
                return taskChatService.saveMessage(tokenData, message);

            case ChatType.ISSUE:
                return issueChatService.saveMessage(tokenData, message);

            case ChatType.PROJECT:
                return projectChatService.saveMessage(tokenData, message);

            case ChatType.DIALOG:
                return dialogChatService.saveMessage(tokenData, message);
        }
        return null;
    }

    private void sendMessageToMQ(ChatTokenData tokenData, MessageDTO dto)
            throws JsonProcessingException, IOException {
        String chatType = tokenData.chatType;
        Long chatId = tokenData.chatId;
        switch (chatType) {
            case ChatType.TASK:
                taskChatSender.send(convertMessagesToJson(Arrays.asList(dto), chatType, chatId));
                break;

            case ChatType.ISSUE:
                issueChatSender.send(convertMessagesToJson(Arrays.asList(dto), chatType, chatId));
                break;

            case ChatType.PROJECT:
                projectChatSender.send(convertMessagesToJson(Arrays.asList(dto), chatType, chatId));
                break;

            case ChatType.DIALOG:
                dialogChatSender.send(convertMessagesToJson(Arrays.asList(dto), chatType, chatId));
                break;
        }
    }

    private void flushMessageQueue(Session session, String chatType, long chatId) throws IOException {
        switch (chatType) {
            case ChatType.TASK:
                ConcurrentLinkedQueue<String> taskMessageQueue = taskChatIdToMessageQueue.get(chatId);
                if (taskMessageQueue != null) {
                    while (!taskMessageQueue.isEmpty()) {
                        session.getBasicRemote().sendText(taskMessageQueue.remove());
                    }
                }
                break;

            case ChatType.ISSUE:
                ConcurrentLinkedQueue<String> issueMessageQueue = issueChatIdToMessageQueue.get(chatId);
                if (issueMessageQueue != null) {
                    while (!issueMessageQueue.isEmpty()) {
                        session.getBasicRemote().sendText(issueMessageQueue.remove());
                    }
                }
                break;

            case ChatType.PROJECT:
                ConcurrentLinkedQueue<String> projectMessageQueue = projectChatIdToMessageQueue.get(chatId);
                if (projectMessageQueue != null) {
                    while (!projectMessageQueue.isEmpty()) {
                        session.getBasicRemote().sendText(projectMessageQueue.remove());
                    }
                }
                break;

            case ChatType.DIALOG:
                ConcurrentLinkedQueue<String> dialogMessageQueue = dialogChatIdToMessageQueue.get(chatId);
                if (dialogMessageQueue != null) {
                    while (!dialogMessageQueue.isEmpty()) {
                        session.getBasicRemote().sendText(dialogMessageQueue.remove());
                    }
                }
                break;
        }
    }

    @Override
    public void sendMessageFromMQ(String chatType, long chatId, String message) {
        try {
            SecurityTokenData tokenData = null;
            switch (chatType) {
                case ChatType.TASK:
                    tokenData = taskChatIdToToken.get(chatId);
                    break;

                case ChatType.ISSUE:
                    tokenData = issueChatIdToToken.get(chatId);
                    break;

                case ChatType.PROJECT:
                    tokenData = projectChatIdToToken.get(chatId);
                    break;

                case ChatType.DIALOG:
                    tokenData = dialogChatIdToToken.get(chatId);
                    break;

                default:
                    return;
            }

            boolean valid = securityService.validateTokenData(tokenData, false);
            if (valid) {
                session.getBasicRemote().sendText(message);
            } else {
                switch (chatType) {
                    case ChatType.TASK:
                        if (taskChatIdToMessageQueue.get(chatId) == null) { // TODO: fix concurrency
                            taskChatIdToMessageQueue.put(chatId, new ConcurrentLinkedQueue<String>());
                        }
                        taskChatIdToMessageQueue.get(chatId).add(message);
                        break;

                    case ChatType.ISSUE:
                        if (issueChatIdToMessageQueue.get(chatId) == null) { // TODO: fix concurrency
                            issueChatIdToMessageQueue.put(chatId, new ConcurrentLinkedQueue<String>());
                        }
                        issueChatIdToMessageQueue.get(chatId).add(message);
                        break;

                    case ChatType.PROJECT:
                        if (projectChatIdToMessageQueue.get(chatId) == null) { // TODO: fix concurrency
                            projectChatIdToMessageQueue.put(chatId, new ConcurrentLinkedQueue<String>());
                        }
                        projectChatIdToMessageQueue.get(chatId).add(message);
                        break;

                    case ChatType.DIALOG:
                        if (dialogChatIdToMessageQueue.get(chatId) == null) { // TODO: fix concurrency
                            dialogChatIdToMessageQueue.put(chatId, new ConcurrentLinkedQueue<String>());
                        }
                        dialogChatIdToMessageQueue.get(chatId).add(message);
                        break;
                }

                session.getBasicRemote().sendText(JsonContainer.fromResponseType(ResponseType.REFRESH_TOKEN));
            }
        } catch (Exception e) {
            logAndSendError(session, e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("onClose::" + closeReason.getReasonPhrase() + "; SessionId=" + session.getId());
        }

        taskChatListener.removeController(taskChatIdToToken.keySet(), session.getId());
        taskChatIdToToken.clear();

        issueChatListener.removeController(issueChatIdToToken.keySet(), session.getId());
        issueChatIdToToken.clear();

        projectChatListener.removeController(projectChatIdToToken.keySet(), session.getId());
        projectChatIdToToken.clear();

        projectChatListener.removeController(dialogChatIdToToken.keySet(), session.getId());
        projectChatIdToToken.clear();
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
