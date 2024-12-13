package com.example.global.security;

import com.example.domain.user.service.UserService;
import com.example.global.rsData.RsData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 인증이 필요 없는 경로 처리
        if (isPermitAllPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // accessToken 추출
            String accessToken = extractCookieValue(request, "accessToken");

            if (accessToken != null && !accessToken.isBlank()) {
                logger.debug("Access Token 검증 중: {}", accessToken);

                // AccessToken 검증
                if (!userService.validateToken(accessToken)) {
                    logger.debug("Access Token이 만료됨. Refresh Token을 검증 중...");
                    handleRefreshToken(request, response);
                } else {
                    // 유효한 accessToken이면 사용자 인증
                    authenticateUser(accessToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT 처리 중 오류 발생", e);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPermitAllPath(String requestURI) {
        return requestURI.startsWith("/api/v1/quizshow") ||
                requestURI.contains("/api/v1/user/login") ||
                requestURI.contains("/api/v1/user/register") ||
                requestURI.equals("/api/v1/user/logout");
    }

    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            logger.debug("요청에 쿠키를 찾을 수 없음");
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookieValue(request, "refreshToken");

        if (refreshToken != null && !refreshToken.isBlank()) {
            logger.debug("Refresh Token 검증 중: {}", refreshToken);

            RsData<String> refreshResult = userService.refreshAccessToken(refreshToken);
            if (refreshResult.isSuccess()) {
                logger.debug("Access Token 재발급 성공");
                addCookie(response, "accessToken", refreshResult.getData());
            } else {
                logger.debug("Refresh Token이 유효하지 않음");
            }
        }
    }

    private void authenticateUser(String accessToken) {
        SecurityUser securityUser = userService.getUserFromAccessToken(accessToken);
        if (securityUser != null) {
            logger.debug("사용자 인증 성공: {}", securityUser.getUsername());
            SecurityContextHolder.getContext().setAuthentication(securityUser.genAuthentication());
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
