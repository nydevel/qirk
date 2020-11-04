package org.wrkr.clb.statistics.services;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

public interface EventService extends InitializingBean {

    public String getCode();

    public void onMessage(Map<String, Object> requestBody) throws Exception;
}
