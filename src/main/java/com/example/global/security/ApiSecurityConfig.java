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
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
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


                                .requestMatchers(HttpMethod.GET,"/api/*/quizshow/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/*/quizshow/*/submit").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/*/articles").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/*/articles/*").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/*/user/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/*/user/logout").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/*/user/register").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization"); // Authorization 헤더 노출

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}