package com.example.domain.user.controller;

import com.example.domain.user.dto.request.UserRequest;
import com.example.domain.user.dto.response.UserResponse;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import com.example.global.Jwt.JwtService;
import com.example.global.Jwt.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/User")
public class UserController {
    private final UserService userService;
    private JwtService jwtService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = this.userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody UserRequest request) {
        String token = userService.authenticateAndGenerateToken(request);
        return ResponseEntity.ok(new TokenResponse(token));
    }

 
//        // accessToken 발급
//        String accessToken = jwtProvider.genAccessToken(user);
//        Cookie cookie = new Cookie("accessToken", accessToken);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);

//
//        response.addCookie(cookie);
//        return RsData.of("200", "토큰 발급 성공" + accessToken, new MemberResponse(member));

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        UserResponse user = userService.getUserFromToken(token);
        return ResponseEntity.ok(user);
    }


    // 사용자 정보 조회
    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        return userService.findUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 사용자 정보 수정
    @PatchMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody SiteUser updatedData) {
        SiteUser existingUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        SiteUser updatedUser = userService.updateUser(existingUser, updatedData);
        return ResponseEntity.ok(updatedUser);
    }

    // 사용자 삭제
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("사용자가 삭제되었습니다.");
    }
}
