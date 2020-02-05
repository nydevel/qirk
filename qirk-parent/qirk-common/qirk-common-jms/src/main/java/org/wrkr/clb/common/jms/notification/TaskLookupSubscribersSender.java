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
package org.wrkr.clb.common.jms.notification;

import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

public class TaskLookupSubscribersSender {

    private static final Logger LOG = LoggerFactory.getLogger(TaskLookupSubscribersSender.class);

    @Autowired
    @Qualifier("taskLookupSubscribersJmsTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("jmsTaskLookupSubscribersQueue")
    private Queue queue;

    public void send(TaskCommentMessage message) {
        long startTime = System.currentTimeMillis();

        try {
            String json = message.toJson();
            jmsTemplate.send(queue, s -> s.createTextMessage(json));
        } catch (Exception e) {
            LOG.error("Could not send message " + message + " to mq", e);
        }

        if (LOG.isDebugEnabled()) {
            long resultTime = System.currentTimeMillis() - startTime;
            LOG.debug("processed jms send for message with type " + message.type + " in " +
                    resultTime + " ms");
        }
    }
}
