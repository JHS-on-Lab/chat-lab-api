package me.son.chatlabapi.global.util;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static void addHttpOnlyCookie(HttpServletResponse response, String name, String value, long maxAgeSeconds, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure) // 운영(HTTPS) 환경에서는 true
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteCookie(HttpServletResponse response, String name, boolean secure) {
        addHttpOnlyCookie(response, name, "", 0, secure);
    }
}
