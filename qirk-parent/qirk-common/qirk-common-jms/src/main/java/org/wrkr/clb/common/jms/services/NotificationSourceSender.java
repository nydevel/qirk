package org.wrkr.clb.common.jms.services;

import java.util.Map;

import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.wrkr.clb.common.jms.message.notification.BaseNotificationMessage;
import org.wrkr.clb.common.util.strings.JsonUtils;

public class NotificationSourceSender {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationSourceSender.class);

    @Autowired
    @Qualifier("notificationSourceJmsTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("jmsNotificationQueue")
    private Queue queue;

    public void send(BaseNotificationMessage message) {
        long startTime = System.currentTimeMillis();
        if (message.subscriberIds.isEmpty()) {
            return;
        }

        try {
            String json = message.toJson();
            jmsTemplate.send(queue, s -> s.createTextMessage(json));
        } catch (Exception e) {
            LOG.error("Could not send message " + message + " to mq", e);
        }

        if (LOG.isInfoEnabled()) {
            long resultTime = System.currentTimeMillis() - startTime;
            LOG.info("processed jms send for message with type " + message.type + " in " +
                    resultTime + " ms");
        }
    }

    public void send(Map<String, Object> message) {
        long startTime = System.currentTimeMillis();

        try {
            String json = JsonUtils.convertMapToJson(message);
            jmsTemplate.send(queue, s -> s.createTextMessage(json));
        } catch (Exception e) {
            LOG.error("Could not send message " + message + " to mq", e);
        }

        if (LOG.isDebugEnabled()) {
            long resultTime = System.currentTimeMillis() - startTime;
            LOG.debug("processed jms send for message with type " + message.get(BaseNotificationMessage.TYPE) + " in " +
                    resultTime + " ms");
        }
    }
}
