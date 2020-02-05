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
package org.wrkr.clb.statistics.services.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;
import org.wrkr.clb.common.jms.message.statistics.NotificationUnsubscriptionMessage;
import org.wrkr.clb.statistics.repo.user.NotificationUnsubscriptionRepo;
import org.wrkr.clb.statistics.services.BaseEventService;

public class NotificationUnsubscriptionService extends BaseEventService {

    @Autowired
    private NotificationUnsubscriptionRepo notifUnsubscriptionRepo;

    @Override
    public String getCode() {
        return BaseStatisticsMessage.Code.NOTIFICATION_UNSUBSCRIPTION;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) {
        notifUnsubscriptionRepo.save((Long) requestBody.get(NotificationUnsubscriptionMessage.USER_ID),
                (String) requestBody.get(NotificationUnsubscriptionMessage.NOTIFICATION_TYPE));
    }
}
