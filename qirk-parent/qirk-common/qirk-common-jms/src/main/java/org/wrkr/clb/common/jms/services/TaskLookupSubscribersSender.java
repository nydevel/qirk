package org.wrkr.clb.common.jms.services;

import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.wrkr.clb.common.jms.message.notification.TaskCommentMessage;

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
