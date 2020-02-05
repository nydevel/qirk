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
package org.wrkr.clb.chat.services.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.repo.sql.SQLChatDatabaseRepo;
import org.wrkr.clb.common.mail.DevOpsMailService;

@Component("chatSelfCheckJobService")
@EnableScheduling
public class SelfCheckJobService {

    @Autowired
    private DevOpsMailService mailService;

    @Autowired
    private SQLChatDatabaseRepo chatRepo;

    public void checkChat() {
        try {
            chatRepo.check();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("clb_chat database", e);
        }
    }
}
