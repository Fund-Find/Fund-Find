package com.example.domain.auth.controller;

import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import com.example.global.Jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        SiteUser user = userService.authenticate(request.getUsername(), request.getPassword());
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid TokenRequest request) {
        String refreshedToken = jwtService.refreshToken(request.getToken());
        return ResponseEntity.ok(new AuthResponse(refreshedToken));
    }
}
