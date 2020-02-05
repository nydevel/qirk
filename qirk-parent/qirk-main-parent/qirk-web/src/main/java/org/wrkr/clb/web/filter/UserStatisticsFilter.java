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
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.wrkr.clb.common.jms.message.statistics.NewUserMessage;
import org.wrkr.clb.common.jms.services.StatisticsSender;
import org.wrkr.clb.services.http.CookieService;
import org.wrkr.clb.services.util.http.Cookies;
import org.wrkr.clb.services.util.http.SessionAttribute;


@Component
public class UserStatisticsFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(UserStatisticsFilter.class);

    private static final int USER_STATISTICS_COOKIE_LIFETIME_SECONDS = 10 * 366 * 34 * 60 * 60; // 10 years
    private static final long USER_STATISTICS_COOKIE_RELEVANCE_MILLIS = 7 * 24 * 60 * 60 * 1000; // 7 days

    private static final String HEARTBEAT_URI = "/api/heartbeat";

    private CookieService cookieService;
    private StatisticsSender userStatisticsSender;

    @Override
    public void init(@SuppressWarnings("unused") FilterConfig filterConfig) {
        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        cookieService = ctx.getBean(CookieService.class);
        userStatisticsSender = ctx.getBean(StatisticsSender.class);
    }

    private HttpServletResponse setUserStatisticsCookie(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String userStatisticsCookie = cookieService.getCookieValue(httpRequest, Cookies.USER_STAT);
        String cookieUuid = null;
        Long cookieTimestamp = null;
        if (userStatisticsCookie != null) {
            Integer lastColumnIndex = userStatisticsCookie.lastIndexOf(':');
            if (lastColumnIndex >= 0) {
                cookieUuid = userStatisticsCookie.substring(0, lastColumnIndex);
                try {
                    cookieTimestamp = Long.parseLong(userStatisticsCookie.substring(lastColumnIndex + 1));
                } catch (NumberFormatException e) {
                    LOG.error("Exception caught at filter", e);
                }
            }
        }

        long now = System.currentTimeMillis();
        if (cookieTimestamp == null || cookieTimestamp >= now) {
            if (cookieUuid == null) {
                cookieUuid = UUID.randomUUID().toString();
            }
            cookieTimestamp = now + USER_STATISTICS_COOKIE_RELEVANCE_MILLIS;
            userStatisticsSender.send(new NewUserMessage(cookieUuid, now));
            httpResponse = cookieService.addCookie(httpResponse, Cookies.USER_STAT, cookieUuid + ":" + cookieTimestamp,
                    USER_STATISTICS_COOKIE_LIFETIME_SECONDS, true);
        }

        return httpResponse;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        if (session.getAttribute(SessionAttribute.AUTHN_USER) == null && !httpRequest.getRequestURI().startsWith(HEARTBEAT_URI)) {
            httpResponse = setUserStatisticsCookie(httpRequest, httpResponse);
        }

        chain.doFilter(httpRequest, httpResponse);
    }
}
