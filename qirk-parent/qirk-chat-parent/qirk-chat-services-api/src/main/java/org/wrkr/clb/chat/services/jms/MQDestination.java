package org.wrkr.clb.chat.services.jms;

public interface MQDestination {

    public void sendMessageFromMQ(String chatType, long chatId, String message);
}
