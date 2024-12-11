package com.example.global.security;


import com.example.domain.user.service.UserService;
import com.example.global.rsData.RsData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final UserService userService;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String authorizationHeader = request.getHeader("Authorization");
        logger.debug("Incoming Authorization Header: {}", String.valueOf(authorizationHeader));

        if (request.getRequestURI().contains("/api/v1/user") || request.getRequestURI().equals("/api/v1/user/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = _getCookie("accessToken");
        // accessToken 검증 or refreshToken 발급
        if (!accessToken.isBlank()) {
            // 토큰 유효기간 검증
            if (!userService.validateToken(accessToken)) {
                String refreshToken = _getCookie("refreshToken");

                RsData<String> rs = userService.refreshAccessToken(refreshToken);
                _addHeaderCookie("accessToken", rs.getData());
            }

            // securityUser 가져오기
            SecurityUser securityUser = userService.getUserFromAccessToken(accessToken);
            // 인가 처리
            SecurityContextHolder.getContext().setAuthentication(securityUser.genAuthentication());
        }

        filterChain.doFilter(request, response);
    }

    private String _getCookie(String name) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) {
            logger.debug("요청에 쿠키를 찾을 수 없음");
            return ""; // 쿠키가 없는 경우 빈 문자열 반환
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(""); // 원하는 쿠키가 없는 경우 빈 문자열 반환
    }


    private void _addHeaderCookie(String tokenName, String token) {
        ResponseCookie cookie = ResponseCookie.from(tokenName, token)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }
}