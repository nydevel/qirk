package org.wrkr.clb.services.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CookieService {

    public HttpServletResponse addCookie(HttpServletResponse response, String name, String value, Integer expiry,
            boolean httpOnly);

    public Cookie getCookie(HttpServletRequest request, String name);

    public String getCookieValue(HttpServletRequest request, String name);

    public HttpServletResponse removeCookie(HttpServletResponse response, Cookie cookie);
}
