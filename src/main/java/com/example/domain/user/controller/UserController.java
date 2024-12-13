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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60);
        res.addCookie(accessTokenCookie);


        String refreshToken = user.getRefreshToken();
        Cookie refreshTokenCookie  = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60);
        res.addCookie(refreshTokenCookie);
        // JWT에서 만료 시간 추출 또는 별도 메서드에서 만료 시간 계산 후 응답에 담기
        long expirationTime = jwtProvider.getExpirationTime(accessToken);

        // 응답에 토큰 만료 시간이나 필요한 정보를 포함
        UserResponse userResponse = new UserResponse(user);
        userResponse.setExpirationTime(expirationTime);

        System.out.println("로그인 요청시 refresh 토큰 :" + refreshToken);
        System.out.println("로그인 요청시 access 토큰 :" + accessToken);
        System.out.println("로그인 요청시 남은 시간 :" + expirationTime);
        return RsData.of("200", "토큰 발급 성공: " + accessToken , userResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse res) {
        if (!jwtProvider.verify(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 유효하지 않습니다.");
        }

        try {
            String newAccessToken = jwtProvider.refreshAccessToken(refreshToken);

            Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 10); // 10분
            res.addCookie(accessTokenCookie);

            // 새로 발급한 AccessToken의 만료 시간 가져오기
            long newExpirationTime = jwtProvider.getExpirationTime(newAccessToken);

            // JSON 형태로 응답 반환
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("resultCode", "200");
            responseData.put("msg", "Access Token 재발급 성공");

            Map<String, Object> data = new HashMap<>();
            data.put("expirationTime", newExpirationTime);
            data.put("accessToken", newAccessToken);

            responseData.put("data", data);

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Access Token 재발급 실패");
        }
    }


}
