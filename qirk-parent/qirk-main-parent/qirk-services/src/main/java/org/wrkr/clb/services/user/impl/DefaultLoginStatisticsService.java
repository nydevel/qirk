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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.common.util.strings.ExtStringUtils;
import org.wrkr.clb.model.user.LoginStatistics;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.LoginStatisticsRepo;
import org.wrkr.clb.services.user.LoginStatisticsService;

@Service
public class DefaultLoginStatisticsService implements LoginStatisticsService {

    @Autowired
    private LoginStatisticsRepo loginStatisticsRepo;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public LoginStatistics create(User user, String forwardedFor) {
        String internetAddress = (forwardedFor == null ? "" : ExtStringUtils.substringByFirstSymbol(forwardedFor, ','));

        LoginStatistics loginStatistics = new LoginStatistics();
        loginStatistics.setInternetAddress(internetAddress);
        loginStatistics.setUser(user);
        loginStatistics.setLoginAt(DateTimeUtils.now());

        loginStatisticsRepo.save(loginStatistics);
        return loginStatistics;
    }
}
