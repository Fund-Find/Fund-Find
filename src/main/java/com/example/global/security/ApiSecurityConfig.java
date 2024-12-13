package com.example.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    @Bean
    SecurityFilterChain apifilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeRequests(
                        authorizeRequests -> authorizeRequests
//                                .requestMatchers(HttpMethod.GET, "/api/*/articles").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/*/articles/*").permitAll()
//                                .requestMatchers(HttpMethod.POST, "/api/*/user/login").permitAll() // 로그인은 누구나 가능, post 요청만 허용
//                                .requestMatchers(HttpMethod.GET, "/api/*/user/logout").permitAll() // 로그인은 누구나 가능, post 요청만 허용
//                                .requestMatchers(HttpMethod.POST, "/api/*/user/register").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/*/user/profile").permitAll()
//                                .requestMatchers(HttpMethod.PATCH, "/api/*/user/profile").permitAll()
//                                .requestMatchers(HttpMethod.POST, "/api/*/user/*").permitAll() // 로그인은 누구나 가능, post 요청만 허용


                                .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/api/**").permitAll()


                                .anyRequest().authenticated()
                )
                .csrf(
                        csrf -> csrf
                                .disable()
                ) // csrf 토큰 끄기
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // cors 설정 추가
                .httpBasic(
                        httpBasic -> httpBasic.disable()
                ) // httpBasic 로그인 방식 끄기
                .formLogin(
                        formLogin -> formLogin.disable()
                ) // 폼 로그인 방식 끄기
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        jwtAuthorizationFilter, //엑세스 토큰을 이용한 로그인 처리
                        UsernamePasswordAuthenticationFilter.class
                );
        ;
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173"); // 허용할 출처 추가
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 요청 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 및 인증 정보 포함 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 CORS 정책 적용
        return source;
    }
}