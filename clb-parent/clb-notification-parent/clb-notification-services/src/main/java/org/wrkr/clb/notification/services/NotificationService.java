/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.notification.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.common.jms.notification.BaseNotificationMessage;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.notification.repo.NotificationRepo;
import org.wrkr.clb.notification.repo.dto.NotificationDTO;
import org.wrkr.clb.notification.services.jms.NotificationDestinationSender;

import com.fasterxml.jackson.core.JsonProcessingException;


//@Service configured in clb-notification-services-ctx.xml
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private static final int DEFAULT_RETRIES_COUNT = 5;

    // config value
    private Integer listBySubscriberIdLimit = 42;

    public Integer getListBySubscriberIdLimit() {
        return listBySubscriberIdLimit;
    }

    public void setListBySubscriberIdLimit(Integer listBySubscriberIdLimit) {
        this.listBySubscriberIdLimit = listBySubscriberIdLimit;
    }

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private NotificationDestinationSender notificationSender;

    private Map<String, NotificationMailService> typeToMailService = new ConcurrentHashMap<String, NotificationMailService>();

    void addService(NotificationMailService mailService) {
        typeToMailService.put(mailService.getType(), mailService);
    }

    private long saveNotificationForSubscriber(long subscriberId, String notificationType, String json) {
        int retriesCount = DEFAULT_RETRIES_COUNT;
        do {
            long timestamp = System.currentTimeMillis();
            if (notificationRepo.save(subscriberId, timestamp, notificationType, json)) {
                return timestamp;
            }
            retriesCount--;
        } while (retriesCount > 0);

        LOG.error("Could not save notification to cassandra");
        return -1;
    }

    public void onMessage(Map<String, Object> messageBody) throws JsonProcessingException {
        String notificationType = (String) messageBody.remove(BaseNotificationMessage.TYPE);
        NotificationMailService mailService = typeToMailService.get(notificationType);

        if (mailService != null) {
            @SuppressWarnings("unchecked")
            List<String> subscriberEmails = (List<String>) messageBody.remove(BaseNotificationMessage.SUBSCRIBER_EMAILS);
            mailService.sendEmail(subscriberEmails, messageBody);
        }

        @SuppressWarnings("unchecked")
        List<Long> subscriberIds = (List<Long>) messageBody.remove(BaseNotificationMessage.SUBSCRIBER_IDS);
        String json = JsonUtils.convertMapToJson(messageBody);

        for (long subscriberId : subscriberIds) {
            long timestamp = saveNotificationForSubscriber(subscriberId, notificationType, json);
            if (timestamp >= 0) {
                notificationSender.send(new NotificationDTO(subscriberId, timestamp, notificationType, json));
            }
        }
    }

    public List<NotificationDTO> getLastMessages(Long subscriberId, Long timestamp) {
        if (subscriberId == null) {
            return new ArrayList<NotificationDTO>();
        }
        if (timestamp == null) {
            timestamp = System.currentTimeMillis();
        }
        return notificationRepo.listTopSinceTimestampBySubscriberId(subscriberId, timestamp, listBySubscriberIdLimit);
    }
}
