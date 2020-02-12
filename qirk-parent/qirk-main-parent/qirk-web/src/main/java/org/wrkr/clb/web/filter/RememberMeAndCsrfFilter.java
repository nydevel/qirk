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
package org.wrkr.clb.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.services.http.CookieService;
import org.wrkr.clb.services.user.RememberMeTokenService;
import org.wrkr.clb.services.util.http.Cookies;
import org.wrkr.clb.services.util.http.JsonStatusCode;
import org.wrkr.clb.services.util.http.SessionAttribute;
import org.wrkr.clb.web.http.Header;

@Component
public class RememberMeAndCsrfFilter extends ResponseBodyFilter {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(RememberMeAndCsrfFilter.class);

    private static final Set<String> SAFE_HTTP_METHODS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            HttpMethod.GET.toString(), HttpMethod.HEAD.toString(), HttpMethod.OPTIONS.toString())));

    private CookieService cookieService;
    private RememberMeTokenService rememberMeService;
    private JDBCUserRepo userRepo;

    @Override
    public void init(@SuppressWarnings("unused") FilterConfig filterConfig) {
        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        cookieService = ctx.getBean(CookieService.class);
        rememberMeService = ctx.getBean(RememberMeTokenService.class);
        userRepo = ctx.getBean(JDBCUserRepo.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        // remember-me: set user to session
        if (session.getAttribute(SessionAttribute.AUTHN_USER) == null) {
            Cookie rememberMeCookie = cookieService.getCookie(httpRequest, Cookies.REMEMBER_ME);

            if (rememberMeCookie != null) {
                String rememberMeToken = rememberMeCookie.getValue();
                Long userId = (rememberMeToken == null ? null : rememberMeService.getUserIdByRememberMeToken(rememberMeToken));
                User user = (userId == null ? null : userRepo.getByIdForAccount(userId));

                if (user == null) {
                    httpResponse = cookieService.removeCookie(httpResponse, rememberMeCookie);
                } else {
                    session.setAttribute(SessionAttribute.AUTHN_USER, user);
                }
            }
        }

        // csrf validation
        if (!SAFE_HTTP_METHODS.contains(httpRequest.getMethod())) {
            String headerCsrfToken = httpRequest.getHeader(Header.X_CSRF_TOKEN);
            if (headerCsrfToken == null || !headerCsrfToken.equals(session.getAttribute(SessionAttribute.CSRF))) {
                writeJsonToResponse((HttpServletResponse) response, HttpServletResponse.SC_FORBIDDEN,
                        JsonStatusCode.INVALID_CSRF_TOKEN, "Invalid CSRF token.");
                return;
            }
        }

        chain.doFilter(httpRequest, httpResponse);
    }
}
