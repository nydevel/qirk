package org.wrkr.clb.statistics.services;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseEventService implements InitializingBean, EventService {

    @Autowired
    protected EventDispatcherService dispatcherService;

    @Override
    public void afterPropertiesSet() throws Exception {
        dispatcherService.addService(this);
    }
}
