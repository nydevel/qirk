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
package org.wrkr.clb.services.user.impl;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.jdbc.transaction.Executor;
import org.wrkr.clb.common.jdbc.transaction.RetryOnCannotAcquireLock;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.ProfileDTO;
import org.wrkr.clb.services.dto.user.PriofileUpdateDTO;
import org.wrkr.clb.services.user.ProfileRetryWrapperService;
import org.wrkr.clb.services.user.ProfileService;

@Service
public class DefaultProfileRetryWrapperService implements ProfileRetryWrapperService {

    @Autowired
    private ProfileService profileService;

    @Override
    public ProfileDTO updateProfile(HttpSession session, User sessionUser, PriofileUpdateDTO profileDTO)
            throws Exception {
        return RetryOnCannotAcquireLock.<ProfileDTO>exec(new Executor() {
            @SuppressWarnings({ "unchecked", "unused" })
            @Override
            public ProfileDTO exec(int retryNumber) throws Exception {
                return profileService.updateProfile(session, sessionUser, profileDTO);
            }
        });
    }
}
