/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
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
