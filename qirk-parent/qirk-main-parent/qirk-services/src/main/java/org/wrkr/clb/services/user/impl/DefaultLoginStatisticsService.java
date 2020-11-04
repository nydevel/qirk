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
