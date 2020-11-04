package org.wrkr.clb.services.user.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.HashEncoder;
import org.wrkr.clb.model.auth.RememberMeToken;
import org.wrkr.clb.model.user.FailedLoginAttempt;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.FailedLoginAttemptRepo;
import org.wrkr.clb.services.dto.user.LoginDTO;
import org.wrkr.clb.services.http.CookieService;
import org.wrkr.clb.services.user.AuthnService;
import org.wrkr.clb.services.user.LoginStatisticsService;
import org.wrkr.clb.services.user.ProfileService;
import org.wrkr.clb.services.user.RememberMeTokenService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.TooManyLoginAttemptsException;
import org.wrkr.clb.services.util.http.Cookies;
import org.wrkr.clb.services.util.http.SessionAttribute;

//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultAuthnService implements AuthnService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAuthnService.class);

    private static final long FAILED_LOGIN_ATTEMPT_LIFETIME_MILLIS = 5 * 60 * 1000; // 5 minutes

    // config value
    private Integer maxFailedLoginAttempts;
    private Integer rememberMeTokenLifetimeSeconds;

    public void setMaxFailedLoginAttempts(Integer maxFailedLoginAttempts) {
        this.maxFailedLoginAttempts = maxFailedLoginAttempts;
    }

    public void setRememberMeTokenLifetimeSeconds(Integer rememberMeTokenLifetimeSeconds) {
        this.rememberMeTokenLifetimeSeconds = rememberMeTokenLifetimeSeconds;
    }

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FailedLoginAttemptRepo failedLoginAttemptRepo;

    @Autowired
    private LoginStatisticsService loginStatisticsService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private RememberMeTokenService rememberMeService;

    @Override
    public HttpServletResponse login(HttpServletResponse response, HttpSession session,
            LoginDTO loginDTO, String forwardedFor) throws AuthenticationException, BadRequestException {
        User user = profileService.getAccount(loginDTO);
        if (user == null) {
            throw new UsernameNotFoundException("");
        }

        List<FailedLoginAttempt> lastAttempts = failedLoginAttemptRepo.listTopRecentByUserId(
                user.getId(), maxFailedLoginAttempts);
        if (lastAttempts.size() >= maxFailedLoginAttempts) {
            FailedLoginAttempt topAttempt = lastAttempts.get(maxFailedLoginAttempts - 1);
            long retryAfterMillis = FAILED_LOGIN_ATTEMPT_LIFETIME_MILLIS -
                    (System.currentTimeMillis() - topAttempt.getFailedAt());

            if (retryAfterMillis > 0L) {
                throw new TooManyLoginAttemptsException(retryAfterMillis, "");
            }
        }

        if (!user.getPasswordHash().equals(HashEncoder.encryptToHex(loginDTO.password))) {
            FailedLoginAttempt attempt = new FailedLoginAttempt();
            attempt.setUserId(user.getId());
            attempt.setFailedAt(System.currentTimeMillis());
            failedLoginAttemptRepo.save(attempt);

            throw new BadCredentialsException("");
        }

        return login(response, session, user, forwardedFor);
    }

    @Override
    public HttpServletResponse login(HttpServletResponse response, HttpSession session, User user, String forwardedFor) {
        try {
            failedLoginAttemptRepo.deleteByUserId(user.getId());
        } catch (Exception e) {
            LOG.error("Could not delete failed login attempts for user " + user.getId(), e);
        }

        try {
            loginStatisticsService.create(user, forwardedFor);
        } catch (Exception e) {
            LOG.error("Could not save login statistics for user " + user.getId(), e);
        }

        session.setAttribute(SessionAttribute.AUTHN_USER, user);
        // remember me
        RememberMeToken token = rememberMeService.rememberMe(user);
        response = cookieService.addCookie(response, Cookies.REMEMBER_ME, token.getToken(),
                rememberMeTokenLifetimeSeconds, true);
        // remember me

        return response;
    }
}
