package org.wrkr.clb.services.user;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.model.user.LoginStatistics;
import org.wrkr.clb.model.user.User;

public interface LoginStatisticsService {

    LoginStatistics create(@NotNull(message = "user must not be null") User user,
            String forwardedFor);
}
