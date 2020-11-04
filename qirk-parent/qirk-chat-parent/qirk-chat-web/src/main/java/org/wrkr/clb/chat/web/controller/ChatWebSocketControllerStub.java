package org.wrkr.clb.chat.web.controller;

import java.io.EOFException;

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
import org.wrkr.clb.chat.services.util.json.JsonStatusCode;
import org.wrkr.clb.chat.web.json.JsonContainer;

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
