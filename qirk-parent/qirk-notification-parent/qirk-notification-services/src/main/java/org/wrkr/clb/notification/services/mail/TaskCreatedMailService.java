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
package org.wrkr.clb.notification.services.mail;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.jms.message.notification.BaseNotificationMessage;
import org.wrkr.clb.common.mail.UserMailService;
import org.wrkr.clb.notification.services.BaseNotificationMailService;


@Service
public class TaskCreatedMailService extends BaseNotificationMailService {

    @Autowired
    private UserMailService mailService;

    @Override
    public String getType() {
        return BaseNotificationMessage.Type.TASK_CREATED;
    }

    @Override
    public void sendEmail(List<String> recipients, Map<String, Object> messageBody) {
        mailService.sendTaskCreatedEmail(recipients, messageBody);
    }
}
