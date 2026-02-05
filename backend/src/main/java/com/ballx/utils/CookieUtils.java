package com.ballx.utils;

import java.util.Base64;
import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CookieUtils {

	public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return Optional.of(cookie);
				}
			}
		}
		return Optional.empty();
	}

	public static void addCookie(
		HttpServletResponse response,
		String name,
		String value,
		int maxAge
	) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static void addSecureCookie(
		HttpServletResponse response,
		String name,
		String value,
		int maxAge
	) {
		ResponseCookie cookie = ResponseCookie.from(name, value)
			.path("/")
			.maxAge(maxAge)
			.httpOnly(true)
			.secure(true)
			.sameSite("Lax")
			.build();

		response.addHeader("Set-Cookie", cookie.toString());
	}

	public static void deleteCookie(
		HttpServletRequest request,
		HttpServletResponse response,
		String name
	) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}

	public static String serialize(Object object) {
		return Base64.getUrlEncoder()
			.encodeToString(SerializationUtils.serialize(object));
	}

	public static <T> T deserialize(Cookie cookie, Class<T> cls) {
		try {
			return cls.cast(SerializationUtils.deserialize(
				Base64.getUrlDecoder().decode(cookie.getValue())
			));
		} catch (Exception e) {
			log.error("Failed to deserialize cookie: {}", cookie.getName(), e);
			return null;
		}
	}
}