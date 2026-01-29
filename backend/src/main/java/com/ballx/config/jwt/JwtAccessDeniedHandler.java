package com.ballx.config.jwt;

import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	private final HandlerExceptionResolver resolver;

	public JwtAccessDeniedHandler(
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException) {

		Exception ex = (Exception)request.getAttribute("exception");
		if (ex == null)
			ex = new CustomException(ErrorCode.AUTH_PERMISSION_DENIED);

		resolver.resolveException(request, response, null, ex);
	}
}
