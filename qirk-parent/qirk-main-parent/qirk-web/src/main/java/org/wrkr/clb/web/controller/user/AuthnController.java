package org.wrkr.clb.web.controller.user;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.AccountUserDTO;
import org.wrkr.clb.services.dto.user.LoginDTO;
import org.wrkr.clb.services.http.CookieService;
import org.wrkr.clb.services.user.AuthnService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.http.Cookies;
import org.wrkr.clb.services.util.http.SessionAttribute;
import org.wrkr.clb.web.controller.BaseAuthenticationExceptionHandlerController;
import org.wrkr.clb.web.http.Header;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "authn", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AuthnController extends BaseAuthenticationExceptionHandlerController {

    @Autowired
    private AuthnService authnService;

    @Autowired
    private CookieService cookieService;

    @GetMapping(value = "check")
    public JsonContainer<AccountUserDTO, Void> check(HttpSession session) {
        User user = (User) session.getAttribute(SessionAttribute.AUTHN_USER);
        return new JsonContainer<AccountUserDTO, Void>(AccountUserDTO.fromEntity(user));
    }

    @PostMapping(value = "login")
    public JsonContainer<Void, Void> login(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            @RequestBody LoginDTO loginDTO) throws AuthenticationException, BadRequestException {
        long startTime = System.currentTimeMillis();

        response = authnService.login(response, session, loginDTO, request.getHeader(Header.X_FORWARDED_FOR));

        logProcessingTimeFromStartTime(startTime, "login");
        return new JsonContainer<Void, Void>();
    }

    @PostMapping(value = "logout")
    public JsonContainer<Void, Void> logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        session.removeAttribute(SessionAttribute.AUTHN_USER);

        Cookie rememberMeCookie = cookieService.getCookie(request, Cookies.REMEMBER_ME);
        if (rememberMeCookie != null) {
            response = cookieService.removeCookie(response, rememberMeCookie);
        }

        return new JsonContainer<Void, Void>();
    }
}
