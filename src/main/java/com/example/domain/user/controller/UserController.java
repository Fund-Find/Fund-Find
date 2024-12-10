package com.example.domain.user.controller;

import com.example.domain.user.dto.request.UserLoginRequest;
import com.example.domain.user.dto.request.UserPatchRequest;
import com.example.domain.user.dto.request.UserRequest;
import com.example.domain.user.dto.response.UserResponse;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import com.example.global.Jwt.JwtProvider;
import com.example.global.rsData.RsData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    // 회원가입

    @PostMapping("/register")
    public RsData<UserResponse> registerUser(
            @RequestPart("userRequest") @Valid UserRequest userRequest,  // JSON 데이터를 받는 부분
            @RequestPart(value = "thumbnailImg", required = false) MultipartFile thumbnailImg) {  // 파일 업로드 처리
        userRequest.setThumbnailImg(thumbnailImg);  // 파일을 UserRequest에 설정
        System.out.println("test");

        SiteUser user = this.userService.registerUser(userRequest);
        return RsData.of("200", "회원가입이 완료되었습니다.", new UserResponse(user));
    }

    @PostMapping("/login")
    public RsData<UserResponse> login (@Valid @RequestBody UserLoginRequest Request, HttpServletResponse res) {

        SiteUser user = this.userService.getUser(Request.getUsername());

        String accessToken = jwtProvider.genAccessToken(user);
        Cookie accessTokenCookie  = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60);
        res.addCookie(accessTokenCookie);


        String refreshToken = user.getRefreshToken();
        Cookie refreshTokenCookie  = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60);
        res.addCookie(refreshTokenCookie);

        System.out.println(refreshToken);
        System.out.println(accessToken);
        return RsData.of("200", "토큰 발급 성공: " + accessToken , new UserResponse(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        res.addCookie(accessTokenCookie);
        res.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/profile")
    public RsData<UserResponse> getProfile(@CookieValue(value = "accessToken", required = false) String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        if (accessToken == null) {
            System.out.println("Access Token이 쿠키에서 발견되지 않았습니다.");
            return RsData.of("403", "엑세스 토큰이 없습니다.", null);
        }

        System.out.println("Access Token: " + accessToken);

        SiteUser user = userService.getSiteUserFromAccessToken(accessToken);
        if (user == null) {
            System.out.println("Access Token에서 사용자 정보를 찾을 수 없습니다.");
            return RsData.of("404", "사용자를 찾을 수 없습니다.", null);
        }

        System.out.println("사용자 프로필 조회 성공: " + user.getUsername());
        return RsData.of("200", "회원 프로필 접근 완료", new UserResponse(user));
    }

    @PatchMapping("/profile")
    public RsData<UserResponse> updateProfile(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @Valid @ModelAttribute UserPatchRequest updatedData,
            @RequestPart(value = "thumbnailImg", required = false) MultipartFile thumbnailImg) {
        if (accessToken == null) {
            return RsData.of("403", "엑세스 토큰이 없습니다.", null);
        }

        // 토큰에서 사용자 정보 가져오기
        SiteUser existingUser = userService.getSiteUserFromAccessToken(accessToken);

        if (existingUser == null) {
            return RsData.of("404", "사용자를 찾을 수 없습니다.", null);
        }

        // 업데이트 데이터 설정
        updatedData.setThumbnailImg(thumbnailImg);

        // 사용자 정보 업데이트
        UserResponse updatedUser = userService.updateUser(existingUser, updatedData);

        return RsData.of("200", "프로필 업데이트 성공", updatedUser);
    }





    // 사용자 삭제
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("사용자가 삭제되었습니다.");
    }
}
