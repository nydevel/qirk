package org.wrkr.clb.notification.services;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseNotificationMailService implements NotificationMailService {

    @Autowired
    protected NotificationService dispatcherService;

    @Override
    public void afterPropertiesSet() throws Exception {
        dispatcherService.addService(this);
    }
}
