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
package org.wrkr.clb.services.user.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.jdbc.transaction.Executor;
import org.wrkr.clb.common.jdbc.transaction.RetryOnCannotAcquireLock;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.ActivationDTO;
import org.wrkr.clb.services.dto.user.RegisterDTO;
import org.wrkr.clb.services.dto.user.RegisterNoPasswordDTO;
import org.wrkr.clb.services.user.RegistrationRetryWrapperService;
import org.wrkr.clb.services.user.RegistrationService;

@Service
public class DefaultRegistrationRetryWrapperService implements RegistrationRetryWrapperService {

    @Autowired
    private RegistrationService registrationService;

    @Deprecated
    @Override
    public EmailSentDTO register(HttpServletRequest request, RegisterDTO registerDTO) throws Exception {
        return RetryOnCannotAcquireLock.<EmailSentDTO>exec(new Executor() {
            @SuppressWarnings({ "unchecked", "unused" })
            @Override
            public EmailSentDTO exec(int retryNumber) throws Exception {
                return registrationService.register(request, registerDTO);
            }
        });
    }

    @Override
    public EmailSentDTO register(RegisterNoPasswordDTO emailDTO) throws Exception {
        return RetryOnCannotAcquireLock.<EmailSentDTO>exec(new Executor() {
            @SuppressWarnings({ "unchecked", "unused" })
            @Override
            public EmailSentDTO exec(int retryNumber) throws Exception {
                return registrationService.register(emailDTO);
            }
        });
    }

    @Deprecated
    @Override
    public User activate(String token) throws Exception {
        return RetryOnCannotAcquireLock.<User>exec(new Executor() {
            @SuppressWarnings({ "unchecked", "unused" })
            @Override
            public User exec(int retryNumber) throws Exception {
                return registrationService.activate(token);
            }
        });
    }

    @Override
    public User activate(HttpServletRequest request, ActivationDTO activationDTO) throws Exception {
        return RetryOnCannotAcquireLock.<User>exec(new Executor() {
            @SuppressWarnings({ "unchecked", "unused" })
            @Override
            public User exec(int retryNumber) throws Exception {
                return registrationService.activate(request, activationDTO);
            }
        });
    }
}
