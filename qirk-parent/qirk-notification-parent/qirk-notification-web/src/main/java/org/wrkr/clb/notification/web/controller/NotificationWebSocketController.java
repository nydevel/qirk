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
package org.wrkr.clb.notification.web.controller;

import java.io.EOFException;
import java.util.List;
import java.util.Map;

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
import org.wrkr.clb.common.crypto.token.notification.NotificationTokenData;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.notification.repo.dto.NotificationDTO;
import org.wrkr.clb.notification.services.NotificationLastCheckService;
import org.wrkr.clb.notification.services.NotificationService;
import org.wrkr.clb.notification.services.dto.LastCheckDTO;
import org.wrkr.clb.notification.services.jms.MQDestination;
import org.wrkr.clb.notification.services.jms.NotificationDestinationListener;
import org.wrkr.clb.notification.services.json.JsonStatusCode;
import org.wrkr.clb.notification.services.security.SecurityService;
import org.wrkr.clb.notification.web.json.JsonContainer;


@ServerEndpoint(value = "/notification")
public class NotificationWebSocketController implements MQDestination {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationWebSocketController.class);

    public static enum RequestType {
        PING("PING"),
        GET_HISTORY("GET_HISTORY"),
        CHECK_ALL_NOTIFICATIONS("CHECK_ALL_NOTIFICATIONS"),
        UNSUBSCRIBE("UNSUBSCRIBE");

        @SuppressWarnings("unused")
        private final String type;

        RequestType(final String type) {
            this.type = type;
        }
    }

    public static enum ResponseType {
        NOTIFICATIONS("NOTIFICATIONS"),
        CHECKED_ALL_NOTIFICATIONS("CHECKED_ALL_NOTIFICATIONS"),
        UNSUBSCRIBED("UNSUBSCRIBED");

        @SuppressWarnings("unused")
        private final String type;

        ResponseType(final String type) {
            this.type = type;
        }
    }

    private Session session;
    private SecurityService securityService;

    private NotificationDestinationListener notificationListener;
    private NotificationService notificationService;
    private NotificationLastCheckService notificationLastCheckService;

    private boolean subscribed = false;
    private Long subscriberId;

    @Override
    public Long getSubscriberId() {
        return (subscribed ? subscriberId : null);
    }

    public void setSubscriberId(long subscriberId) {
        this.subscriberId = subscriberId;
        subscribed = true;
    }

    @OnOpen
    public void onOpen(Session session, @SuppressWarnings("unused") EndpointConfig config) {
        LOG.debug("onOpen::" + session.getId());
        session.setMaxIdleTimeout(16 * 60 * 1000); // 16 minutes
        this.session = session;

        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        securityService = ctx.getBean(SecurityService.class);

        notificationListener = ctx.getBean(NotificationDestinationListener.class);
        notificationService = ctx.getBean(NotificationService.class);
        notificationLastCheckService = ctx.getBean(NotificationLastCheckService.class);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws Exception {
        LOG.debug("onMessage::From=" + session.getId());

        Map<String, Object> request = JsonUtils.<Object>convertJsonToMapUsingLongForInts(message);
        RequestType requestType = null;
        try {
            requestType = RequestType.valueOf((String) request.get("request_type"));
        } catch (IllegalArgumentException e) {
            session.getBasicRemote().sendText(JsonContainer.fromErrorCode(JsonStatusCode.INVALID_REQUEST_TYPE).toJson());
            return;
        }

        try {
            switch (requestType) {
                case PING:
                    return;

                case GET_HISTORY:
                    subscribe(session, request);
                    getHistory(session, (Long) request.get("timestamp"));
                    break;

                case CHECK_ALL_NOTIFICATIONS:
                    checkAll();
                    break;

                case UNSUBSCRIBE:
                    unsubscribe(session);
                    break;
            }
        } catch (SecurityException e) {
            LOG.debug("onMessage::SecurityException::" + e.getMessage() + "; SessionId=" + session.getId(), e);
            session.getBasicRemote().sendText(JsonContainer.fromErrorCode(e.getMessage(), requestType).toJson());
        } catch (Exception e) {
            logAndSendError(session, e);
        }
    }

    private void subscribe(Session session, Map<String, Object> request) {
        if (getSubscriberId() == null) {
            String token = (String) request.get("token");
            String IV = (String) request.get("IV");

            NotificationTokenData tokenData = new NotificationTokenData(securityService.decryptToken(token, IV));
            securityService.validateTokenDataOrThrowSecurityException(tokenData);
            setSubscriberId(tokenData.subscriberId);
            notificationListener.addController(this, tokenData.subscriberId, session.getId());
        }
    }

    private void getHistory(Session session, Long timestamp) throws Exception {
        Long subscriberId = getSubscriberId();
        List<NotificationDTO> dtoList = notificationService.getLastMessages(subscriberId, timestamp);
        LastCheckDTO meta = notificationLastCheckService.getLastCheckTimestamp(subscriberId);
        String json = JsonContainer.fromObjectsAndMeta(dtoList, meta, ResponseType.NOTIFICATIONS).toJson();
        session.getBasicRemote().sendText(json);
    }

    private void checkAll() throws Exception {
        LastCheckDTO meta = notificationLastCheckService.updateLastCheckTimestamp(getSubscriberId());
        session.getBasicRemote().sendText(JsonContainer.fromMeta(meta, ResponseType.CHECKED_ALL_NOTIFICATIONS).toJson());
    }

    private void unsubscribe(Session session) throws Exception {
        notificationListener.removeController(getSubscriberId(), session.getId());
        subscribed = false; // don't set subscriberId = null to prevent NullPointerException
        session.getBasicRemote().sendText(JsonContainer.fromResponseType(ResponseType.UNSUBSCRIBED).toJson());
    }

    @Override
    public void sendMessageFromMQ(Map<String, Object> notification) {
        if (subscribed) {
            try {
                String json = JsonContainer.fromObject(notification, ResponseType.NOTIFICATIONS).toJson();
                session.getBasicRemote().sendText(json);
            } catch (Exception e) {
                logAndSendError(session, e);
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("onClose::" + closeReason.getReasonPhrase() + "; SessionId=" + session.getId());
        }
        notificationListener.removeController(getSubscriberId(), session.getId());
    }

    private void logAndSendError(Session session, Throwable throwable) {
        LOG.error("onError::" + throwable.getMessage() + "; SessionId=" + session.getId(), throwable);
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(JsonContainer.fromErrorCode(JsonStatusCode.INTERNAL_SERVER_ERROR).toJson());
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
