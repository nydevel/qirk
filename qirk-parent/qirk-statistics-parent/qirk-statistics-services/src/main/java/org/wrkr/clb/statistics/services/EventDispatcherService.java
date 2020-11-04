package org.wrkr.clb.statistics.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;


@Service
public class EventDispatcherService {

    private static final Logger LOG = LoggerFactory.getLogger(EventDispatcherService.class);

    private Map<String, EventService> codeToService = new ConcurrentHashMap<String, EventService>();

    void addService(EventService service) {
        codeToService.put(service.getCode(), service);
    }

    public void onMessage(Map<String, Object> requestBody) throws Exception {
        String code = (String) requestBody.get(BaseStatisticsMessage.CODE);
        EventService service = codeToService.get(code);
        if (service != null) {
            service.onMessage(requestBody);
        } else {
            LOG.warn("Unknown code received: " + code);
        }
    }
}
