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
package org.wrkr.clb.services.jms;

import java.util.List;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.common.jms.message.notification.BaseNotificationMessage;
import org.wrkr.clb.common.jms.message.notification.TaskCommentMessage;
import org.wrkr.clb.common.jms.services.NotificationSourceSender;
import org.wrkr.clb.common.mail.UserMailService;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.project.task.TaskSubscriberService;

public class TaskLookupSubscribersListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(TaskLookupSubscribersListener.class);

    @Autowired
    private TaskSubscriberService subscriberService;

    @Autowired
    private NotificationSourceSender notificationSender;

    @Autowired
    private UserMailService mailService;

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                String text = ((TextMessage) message).getText();

                Map<String, Object> messageBody = JsonUtils.<Object>convertJsonToMapUsingLongForInts(text);
                Long taskId = (Long) messageBody.get(TaskCommentMessage.TASK_ID);

                List<User> subscribers = subscriberService.listWithEmail(
                        taskId, NotificationSettings.Setting.TASK_COMMENTED);
                if (subscribers.isEmpty()) {
                    return;
                }

                messageBody.put(BaseNotificationMessage.SUBSCRIBER_IDS, User.toIds(subscribers));
                notificationSender.send(messageBody);

                mailService.sendTaskCommentedEmail(User.toEmailsIfSendEmailNotifications(subscribers), messageBody);
            } catch (Exception e) {
                LOG.error("Exception caught at listener", e);
                throw new RuntimeException(e);
            }
        } else {
            LOG.error("JMS Message is not instance of TextMessage");
        }
    }
}
