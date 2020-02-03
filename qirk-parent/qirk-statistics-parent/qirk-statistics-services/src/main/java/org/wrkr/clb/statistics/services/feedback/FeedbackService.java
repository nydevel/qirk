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
package org.wrkr.clb.statistics.services.feedback;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.mail.FeedbackMailService;
import org.wrkr.clb.statistics.repo.feedback.FeedbackRepo;
import org.wrkr.clb.statistics.repo.model.feedback.Feedback;
import org.wrkr.clb.statistics.services.BaseEventService;


@Service
public class FeedbackService extends BaseEventService {

    private final static String CODE = "STAT_FEEDBACK";

    // uncomment in full version
    // @Autowired
    @SuppressWarnings("unused")
    private FeedbackRepo feedbackRepo;

    @Autowired
    private FeedbackMailService mailService;

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) throws Exception {
        Long senderId = (Long) requestBody.get(Feedback.SENDER_ID);
        String senderEmail = (String) requestBody.get(Feedback.SENDER_EMAIL);
        String feedback = (String) requestBody.get(Feedback.FEEDBACK);

        // uncomment in full version
        // feedbackRepo.save(senderId, senderEmail, feedback);
        mailService.sendFeedbackEmail(senderId, senderEmail, feedback);
    }
}
