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
package org.wrkr.clb.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.dto.TokenDTO;
import org.wrkr.clb.services.http.CookieService;
import org.wrkr.clb.services.util.http.Cookies;
import org.wrkr.clb.services.util.http.SessionAttribute;
import org.wrkr.clb.web.http.Header;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "csrf", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CsrfController extends BaseExceptionHandlerController {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(CsrfController.class);

    @Autowired
    private HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository;

    @Autowired
    private CookieService cookieService;

    @Autowired
    @Qualifier("csrfBackdoorEnabled")
    private Boolean csrfBackdoorEnabled = false;

    @GetMapping(value = "refresh")
    public JsonContainer<Void, Void> refresh(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) {
        String headerCsrfToken = request.getHeader(Header.X_CSRF_TOKEN);
        if (headerCsrfToken == null || !headerCsrfToken.equals(session.getAttribute(SessionAttribute.CSRF))) {
            String newToken = httpSessionCsrfTokenRepository.generateToken(request).getToken();
            session.setAttribute(SessionAttribute.CSRF, newToken);
            response = cookieService.addCookie(response, Cookies.CSRF, newToken, null, false);
        }
        return new JsonContainer<Void, Void>();
    }

    @Deprecated
    @GetMapping(value = "/")
    public JsonContainer<TokenDTO, Void> get(HttpServletResponse response, HttpSession session) {
        if (!csrfBackdoorEnabled) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        String csrfToken = (String) session.getAttribute(SessionAttribute.CSRF);
        return new JsonContainer<TokenDTO, Void>(new TokenDTO(csrfToken));
    }
}
