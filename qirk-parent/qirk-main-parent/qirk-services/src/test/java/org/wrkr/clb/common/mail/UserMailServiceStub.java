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
package org.wrkr.clb.common.mail;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.common.mail.UserMailService;

@SuppressWarnings("unused")
public class UserMailServiceStub extends UserMailService {

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public EmailSentDTO sendPasswordResetEmail(String emailAddress, String token) {
        return new EmailSentDTO(true);
    }

    @Override
    public EmailSentDTO sendProjectInviteEmail(
            String emailAddress, String senderName, String projectName, String password, String token) {
        return new EmailSentDTO(true);
    }

    @Override
    public void sendTaskCreatedEmail(List<String> recipients, Map<String, Object> messageBody) {
    }

    @Override
    public void sendTaskUpdatedEmail(List<String> recipients, Map<String, Object> messageBody) {
    }

    @Override
    public void sendTaskCommentedEmail(Collection<String> recipients, Map<String, Object> messageBody) {
    }
}
