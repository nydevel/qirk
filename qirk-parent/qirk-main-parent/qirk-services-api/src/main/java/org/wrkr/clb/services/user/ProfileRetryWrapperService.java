package org.wrkr.clb.services.user;

import javax.servlet.http.HttpSession;

import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.ProfileDTO;
import org.wrkr.clb.services.dto.user.PriofileUpdateDTO;

public interface ProfileRetryWrapperService {

    public ProfileDTO updateProfile(HttpSession session, User currentUser, PriofileUpdateDTO profileDTO)
            throws Exception;
}
