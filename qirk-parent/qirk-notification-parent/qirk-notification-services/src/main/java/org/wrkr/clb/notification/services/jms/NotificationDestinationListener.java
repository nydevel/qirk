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
package org.wrkr.clb.notification.services.jms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.notification.model.Notification_;

public class NotificationDestinationListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationDestinationListener.class);

    private static final int DEFAULT_RETRIES_COUNT = 50;

    private Map<Long, ConcurrentHashMap<String, MQDestination>> subscriberIdToSessionIdsToControllers = new ConcurrentHashMap<Long, ConcurrentHashMap<String, MQDestination>>();

    @Override
    public void onMessage(Message message) {
        LOG.debug("onMessage");
        if (message instanceof TextMessage) {
            try {
                String text = ((TextMessage) message).getText();

                Map<String, Object> notification = JsonUtils.<Object>convertJsonToMapUsingLongForInts(text);
                long subscriberId = (Long) notification.remove(Notification_.subscriberId);
                ConcurrentHashMap<String, MQDestination> sessionIdToControllers = subscriberIdToSessionIdsToControllers
                        .get(subscriberId);

                if (sessionIdToControllers != null) {
                    sessionIdToControllers.forEach((key, value) -> {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("sendMessageFromMQ::subscriberId=" + subscriberId + "; sessionId=" + key);
                        }
                        value.sendMessageFromMQ(notification);
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
            subscriberIdToSessionIdsToControllers.forEach((key, value) -> {
                LOG.trace(method + "::chatId= " + key + "; sessionIds= " +
                        value.keySet().toString());
            });
        }
    }

    public void addController(MQDestination controller, long subscriberId, String sessionId) {
        int retriesCount = DEFAULT_RETRIES_COUNT;
        do {
            ConcurrentHashMap<String, MQDestination> emptySessionIdToController = new ConcurrentHashMap<String, MQDestination>();
            ConcurrentHashMap<String, MQDestination> sessionIdToController = subscriberIdToSessionIdsToControllers
                    .putIfAbsent(subscriberId, emptySessionIdToController);
            if (sessionIdToController == null) {
                sessionIdToController = emptySessionIdToController;
            }
            sessionIdToController.put(sessionId, controller);

            // validate that sessionIdToController is still inside
            // subscriberIdToSessionIdsToControllers
            ConcurrentHashMap<String, MQDestination> newSessionIdToController = subscriberIdToSessionIdsToControllers
                    .get(subscriberId);
            if (newSessionIdToController == sessionIdToController) {
                break;
            }
            retriesCount--;
        } while (retriesCount > 0);

        logSubscribedChatIds("addController");
    }

    public void removeController(Long subscriberId, String sessionId) {
        if (subscriberId == null) {
            return;
        }

        ConcurrentHashMap<String, MQDestination> sessionIdToController = subscriberIdToSessionIdsToControllers
                .get(subscriberId);
        if (sessionIdToController != null) {
            sessionIdToController.remove(sessionId);
            if (sessionIdToController.isEmpty()) {
                ConcurrentHashMap<String, MQDestination> emptyMap = new ConcurrentHashMap<String, MQDestination>(
                        sessionIdToController);
                if (emptyMap.isEmpty()) {
                    subscriberIdToSessionIdsToControllers.remove(subscriberId, emptyMap);
                }
            }
        }

        logSubscribedChatIds("removeController");
    }
}
