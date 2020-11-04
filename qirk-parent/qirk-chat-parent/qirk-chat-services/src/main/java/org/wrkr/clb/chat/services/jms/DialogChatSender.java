package org.wrkr.clb.chat.services.jms;

import javax.jms.Topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class DialogChatSender {

    @Autowired
    @Qualifier("dialogJmsTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("jmsDialogChatTopic")
    private Topic topic;

    public void send(String message) {
        jmsTemplate.send(topic, s -> s.createTextMessage(message));
    }
}
