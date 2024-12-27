package com.example.global.security;

import com.example.domain.user.service.UserService;
import com.example.global.rsData.RsData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Authorization 헤더에서 토큰 추출
            String authorizationHeader = request.getHeader("Authorization");
            String accessToken = null;

            // Authorization 헤더가 있으면 Bearer 토큰 추출
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                accessToken = authorizationHeader.substring(7);
            }

            // 쿠키에서도 토큰 확인
            if (accessToken == null) {
                accessToken = extractAccessToken(request);
            }

            if (accessToken != null && !accessToken.isBlank()) {
                handleAccessToken(accessToken, request, response);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("JWT 처리 중 오류 발생", e);
            filterChain.doFilter(request, response);
        }
    }

    private boolean isPermitAllPath(String requestURI) {
        return requestURI.contains("/api/v1/user/login") ||
                requestURI.contains("/api/v1/user/register") ||
                requestURI.equals("/api/v1/user/logout") ||
                requestURI.startsWith("/api/v1/quizshow");
    }

    private String extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void handleAccessToken(String accessToken, HttpServletRequest request, HttpServletResponse response) {
        if (!userService.validateToken(accessToken)) {
            String refreshToken = extractRefreshToken(request);
            if (refreshToken != null) {
                refreshAccessToken(refreshToken, response);
            }
        } else {
            authenticateUser(accessToken);
        }
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void refreshAccessToken(String refreshToken, HttpServletResponse response) {
        RsData<String> refreshResult = userService.refreshAccessToken(refreshToken);
        if (refreshResult.isSuccess()) {
            addAccessTokenCookie(refreshResult.getData(), response);
        }
    }

    private void authenticateUser(String accessToken) {
        SecurityUser securityUser = userService.getUserFromAccessToken(accessToken);
        if (securityUser != null) {
            SecurityContextHolder.getContext().setAuthentication(securityUser.genAuthentication());
        }
    }

    private void addAccessTokenCookie(String token, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}