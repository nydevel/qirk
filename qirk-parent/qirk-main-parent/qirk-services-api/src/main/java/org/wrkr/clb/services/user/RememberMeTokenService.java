package org.wrkr.clb.services.user;

import java.time.OffsetDateTime;

import org.wrkr.clb.model.auth.RememberMeToken;
import org.wrkr.clb.model.user.User;

public interface RememberMeTokenService {

    public RememberMeToken rememberMe(User user);

    public Long getUserIdByRememberMeToken(String token);

    public OffsetDateTime updateRememberMeToken(String token);
}
