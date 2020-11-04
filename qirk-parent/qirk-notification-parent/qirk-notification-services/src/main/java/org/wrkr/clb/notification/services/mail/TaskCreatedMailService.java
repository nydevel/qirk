package org.wrkr.clb.notification.services.mail;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.jms.message.notification.BaseNotificationMessage;
import org.wrkr.clb.common.mail.UserMailService;
import org.wrkr.clb.notification.services.BaseNotificationMailService;


@Service
public class TaskCreatedMailService extends BaseNotificationMailService {

    @Autowired
    private UserMailService mailService;

    @Override
    public String getType() {
        return BaseNotificationMessage.Type.TASK_CREATED;
    }

    @Override
    public void sendEmail(List<String> recipients, Map<String, Object> messageBody) {
        mailService.sendTaskCreatedEmail(recipients, messageBody);
    }
}
