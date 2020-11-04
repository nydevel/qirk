package org.wrkr.clb.services.http.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.wrkr.clb.services.http.CookieService;

@Service
public class DefaultCookieService implements CookieService {

    @Override
    public HttpServletResponse addCookie(HttpServletResponse response, String name, String value, Integer expiry,
            boolean httpOnly) {
        Cookie rememberMeCookie = new Cookie(name, value);
        if (expiry != null) {
            rememberMeCookie.setMaxAge(expiry);
        }
        rememberMeCookie.setHttpOnly(httpOnly);
        rememberMeCookie.setPath("/");
        response.addCookie(rememberMeCookie);

        return response;
    }

    @Override
    public Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = (request == null ? null : request.getCookies());
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    @Override
    public String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return (cookie == null ? null : cookie.getValue());
    }

    @Override
    public HttpServletResponse removeCookie(HttpServletResponse response, Cookie cookie) {
        if (cookie != null) {
            cookie = new Cookie(cookie.getName(), "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        return response;
    }
}
