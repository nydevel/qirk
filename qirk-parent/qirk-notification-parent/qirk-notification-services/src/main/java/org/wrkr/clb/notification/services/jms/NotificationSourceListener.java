package org.wrkr.clb.notification.services.jms;

import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.notification.services.NotificationService;

public class NotificationSourceListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationSourceListener.class);

    @Autowired
    private NotificationService notificationService;

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            LOG.debug("onMessage");
            try {
                String text = ((TextMessage) message).getText();
                Map<String, Object> messageBody = JsonUtils.<Object>convertJsonToMapUsingLongForInts(text);
                notificationService.onMessage(messageBody);
            } catch (Exception e) {
                LOG.error("Exception caught at listener", e);
                throw new RuntimeException(e);
            }
        } else {
            LOG.error("JMS Message is not instance of TextMessage");
        }
    }
}
