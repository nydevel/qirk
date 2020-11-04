package org.wrkr.clb.services.user;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.LoginDTO;
import org.wrkr.clb.services.util.exception.BadRequestException;

@Validated
public interface AuthnService {

    public HttpServletResponse login(HttpServletResponse response, HttpSession session,
            User user, String forwardedFor);

    public HttpServletResponse login(HttpServletResponse response, HttpSession session,
            @Valid LoginDTO loginDTO, String forwardedFor) throws AuthenticationException, BadRequestException;
}
