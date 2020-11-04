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
