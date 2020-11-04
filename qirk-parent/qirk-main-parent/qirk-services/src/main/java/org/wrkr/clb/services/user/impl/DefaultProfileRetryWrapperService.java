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
