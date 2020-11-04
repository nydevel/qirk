package org.wrkr.clb.notification.services.jms;

import java.util.Map;

public interface MQDestination {

    public void sendMessageFromMQ(Map<String, Object> notification);

    public Long getSubscriberId();
}
