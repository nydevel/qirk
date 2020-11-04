package org.wrkr.clb.notification.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

public interface NotificationMailService extends InitializingBean {

    public String getType();

    public void sendEmail(List<String> recipients, Map<String, Object> messageBody);
}
