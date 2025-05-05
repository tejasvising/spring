package com.newpackage.neetcounsel.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.WebUtils;

import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Required if SameSite=None
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        return Optional.ofNullable(WebUtils.getCookie(request, name));
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
        	System.out.println("cookie is not null");
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setSecure(false);
            response.addCookie(cookie);
        }
    }

    public static String serialize(Object object) {
        byte[] bytes = SerializationUtils.serialize(object);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
        Object deserialized = SerializationUtils.deserialize(bytes);
        return cls.cast(deserialized);
    }
}
