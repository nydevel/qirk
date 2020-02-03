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
package org.wrkr.clb.statistics.services.chat;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.jms.statistics.BaseStatisticsMessage;
import org.wrkr.clb.common.jms.statistics.NewMessageStatisticsMessage;
import org.wrkr.clb.statistics.repo.chat.NewMessageRepo;
import org.wrkr.clb.statistics.services.BaseEventService;

@Service
public class NewMessageService extends BaseEventService {

    @Autowired
    private NewMessageRepo newMessageRepo;

    @Override
    public String getCode() {
        return BaseStatisticsMessage.Code.NEW_MESSAGE;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) {
        newMessageRepo.save((String) requestBody.get(NewMessageStatisticsMessage.OWNER_TYPE),
                (Long) requestBody.get(NewMessageStatisticsMessage.OWNER_ID),
                (Long) requestBody.get(NewMessageStatisticsMessage.CREATED_AT));
    }
}
