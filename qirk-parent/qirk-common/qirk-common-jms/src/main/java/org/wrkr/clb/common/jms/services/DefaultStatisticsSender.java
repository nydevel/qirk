package org.wrkr.clb.common.jms.services;

import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;

public class DefaultStatisticsSender implements StatisticsSender {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStatisticsSender.class);

    @Autowired
    @Qualifier("statisticsJmsTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("jmsStatisticsQueue")
    private Queue queue;

    @Override
    public void send(BaseStatisticsMessage message) {
        long startTime = System.currentTimeMillis();

        try {
            String json = message.toJson();
            jmsTemplate.send(queue, s -> s.createTextMessage(json));
        } catch (Exception e) {
            LOG.error("Could not send message " + message + " to mq", e);
        }

        if (LOG.isInfoEnabled()) {
            long resultTime = System.currentTimeMillis() - startTime;
            LOG.info("processed jms send for message with code " + message.code + " in " +
                    resultTime + " ms");
        }
    }
}
