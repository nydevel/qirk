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

import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.wrkr.clb.notification.repo.dto.NotificationDTO;

@Component
public class NotificationDestinationSender {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationDestinationSender.class);

    @Autowired
    @Qualifier("notificationDestinationJmsTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("jmsNotificationTopic")
    private Topic topic;

    public void send(NotificationDTO notification) {
        try {
            String json = notification.toJson();
            jmsTemplate.send(topic, s -> s.createTextMessage(json));
        } catch (Exception e) {
            LOG.error("Could not send message with type " + notification.notificationType + " to mq", e);
        }
    }
}
