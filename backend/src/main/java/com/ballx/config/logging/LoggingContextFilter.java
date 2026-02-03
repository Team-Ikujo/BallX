package com.ballx.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * 로그 컨텍스트 필터
 * - request.id: 요청 고유 ID (추적용)
 * - event.domain: 로그 영역 분류 (S3 라우팅용)
 *
 * 영역 분류 기준:
 * - auth: 인증/인가 (로그인, 토큰)
 * - user: 회원 관리 (가입, 탈퇴, 정보수정)
 * - payment: 결제 (결제, 환불) - 장기 보관 필요
 * - ticket: 티켓팅 (대기열, 좌석, 예매)
 * - system: 시스템 (헬스체크, actuator)
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingContextFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID = "request.id";
    private static final String MDC_EVENT_DOMAIN = "event.domain";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Request ID 설정 (헤더에 있으면 사용, 없으면 생성)
            String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                    .orElse(UUID.randomUUID().toString());
            MDC.put(MDC_REQUEST_ID, requestId);

            // 영역 분류
            String domain = classifyDomain(request.getRequestURI());
            MDC.put(MDC_EVENT_DOMAIN, domain);

            // 응답 헤더에 Request ID 추가 (프론트엔드 디버깅용)
            response.setHeader(REQUEST_ID_HEADER, requestId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    /**
     * URI 기반 영역 분류
     * TODO: API 개발 진행에 따라 경로 추가/수정 필요
     */
    private String classifyDomain(String uri) {
        if (uri == null) {
            return "general";
        }

        // 인증/인가
        if (uri.contains("/auth") || uri.contains("/login") || uri.contains("/oauth") || uri.contains("/token")) {
            return "auth";
        }

        // 회원 관리
        if (uri.contains("/user") || uri.contains("/member") || uri.contains("/profile")) {
            return "user";
        }

        // TODO: 결제 API 개발 후 주석 해제 (장기 보관 필요)
        // if (uri.contains("/payment") || uri.contains("/refund") || uri.contains("/order")) {
        //     return "payment";
        // }

        // TODO: 티켓팅 API 개발 후 주석 해제
        // if (uri.contains("/ticket") || uri.contains("/queue") || uri.contains("/seat") || uri.contains("/reservation")) {
        //     return "ticket";
        // }

        // 시스템
        if (uri.contains("/actuator") || uri.contains("/health") || uri.contains("/ready")) {
            return "system";
        }

        return "general";
    }
}
