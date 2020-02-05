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
package org.wrkr.clb.statistics.services.project;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;
import org.wrkr.clb.common.jms.message.statistics.NewTaskMessage;
import org.wrkr.clb.statistics.repo.project.NewTaskRepo;
import org.wrkr.clb.statistics.services.BaseEventService;


@Service
public class NewTaskService extends BaseEventService {

    @Autowired
    private NewTaskRepo newTaskRepo;

    @Override
    public String getCode() {
        return BaseStatisticsMessage.Code.NEW_TASK;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) {
        newTaskRepo.save((Long) requestBody.get(NewTaskMessage.PROJECT_ID),
                (String) requestBody.get(NewTaskMessage.PROJECT_NAME),
                (Long) requestBody.get(NewTaskMessage.TASK_ID),
                (Long) requestBody.get(NewTaskMessage.CREATED_AT),
                (String) requestBody.get(NewTaskMessage.TYPE),
                (String) requestBody.get(NewTaskMessage.PRIORITY),
                (String) requestBody.get(NewTaskMessage.STATUS));
    }
}
