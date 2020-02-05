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
package org.wrkr.clb.services.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class JMSCheckService {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(JMSCheckService.class);

    private static final String CHECK_STRING = "OK";

    @Autowired
    @Qualifier("jmsConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Autowired
    @Qualifier("jmsSelfCheckQueue")
    private Queue queue;
    @Autowired
    @Qualifier("jmsSelfCheckTopic")
    private Topic topic;

    @Autowired
    private SynchronousMessageSender sender;

    private void check(Destination destination) throws Exception {
        Connection connection = connectionFactory.createConnection();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(destination);

            connection.start();

            sender.send(destination, CHECK_STRING);

            Message message = consumer.receive(500);
            if (message == null) {
                throw new RuntimeException("JMS Message is null");
            }
            if (!(message instanceof TextMessage)) {
                throw new RuntimeException(
                        "JMS Message is an instance of " + message.getClass().getSimpleName() + " instead of TextMessage");
            }

            String response = ((TextMessage) message).getText();
            if (!CHECK_STRING.equals(response)) {
                throw new RuntimeException(
                        "Wrong JMS message received: '" + response + "' instead of '" + CHECK_STRING + "'");
            }

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }

    public void checkQueue() throws Exception {
        check(queue);
    }

    public void checkTopic() throws Exception {
        check(topic);
    }
}
