package com.example.domain.user.controller;

import com.example.domain.user.dto.request.*;
import com.example.domain.user.dto.response.UserResponse;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.EmailService;
import com.example.domain.user.service.UserService;
import com.example.global.Jwt.JwtProvider;
import com.example.global.rsData.RsData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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


        SiteUser user = this.userService.registerUser(userRequest);
        return RsData.of("200", "회원가입이 완료되었습니다.", new UserResponse(user));
    }

    @PostMapping("/login")
    public RsData<UserResponse> login (@Valid @RequestBody UserLoginRequest Request, HttpServletResponse res) {

        SiteUser user = this.userService.getUser(Request.getUsername());
        if (user == null) {
            return RsData.of("404", "사용자를 찾을 수 없습니다.", null);
        }

        // 2. 비밀번호 검증
        boolean isPasswordValid = userService.checkPassword(Request.getPassword(), user.getPassword());
        if (!isPasswordValid) {
            return RsData.of("401", "비밀번호가 일치하지 않습니다.", null);
        }

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

//        System.out.println("사용자 프로필 조회 성공: " + user.getUsername());
//        System.out.println("사용자 프로필 투자성향 번호 : " + user.getPropensity().getPropensityId());
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
    @PostMapping("/find-id")
    public RsData<?> findUserId(@Valid @RequestBody UserEmailRequest emailRequest) {
        String email = emailRequest.getEmail();

        // 이메일로 유저 조회
        SiteUser user = userService.getUserByEmail(email);
        if (user == null) {
            return RsData.of("404", "해당 이메일로 등록된 사용자를 찾을 수 없습니다.", null);
        }

        // 아이디 반환
        return RsData.of("200", "아이디 찾기 성공", Map.of("username", user.getUsername()));
    }

    private final EmailService emailService;


    @PostMapping("/reset-password")
    public RsData<?> requestPasswordReset(@RequestBody PasswordResetRequest resetRequest) {
        String username = resetRequest.getUsername();
        String email = resetRequest.getEmail();

        // 사용자 확인
        SiteUser user = userService.findByUsernameAndEmail(username, email);
        if (user == null) {
            return RsData.of("404", "아이디와 이메일이 일치하는 사용자를 찾을 수 없습니다.", null);
        }

        // 인증 토큰 생성
        String token = userService.createPasswordResetToken(user);

        // 이메일 발송
        emailService.sendPasswordResetEmail(email, token);

        return RsData.of("200", "비밀번호 재설정 이메일이 발송되었습니다.", null);
    }

    @GetMapping(value = "/reset-password/confirm", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String confirmResetPassword(@RequestParam("token") String token) {
        // 1. 토큰 검증
        SiteUser user = userService.verifyPasswordResetToken(token);

        if (user == null) {
            return "<html><body>" +
                    "<h1>비밀번호 재설정 실패</h1>" +
                    "<p>유효하지 않거나 만료된 토큰입니다.</p>" +
                    "</body></html>";
        }

        // 2. 새로운 비밀번호 생성
        String newPassword = userService.generateRandomPassword();

        // 3. 새 비밀번호 저장
        userService.updatePassword(user, newPassword);

        // 4. HTML로 새 비밀번호 표시
        return "<html><body>" +
                "<h1>비밀번호 재설정 완료</h1>" +
                "<p>새로운 비밀번호는 다음과 같습니다:</p>" +
                "<p style='font-weight: bold; color: green;'>" + newPassword + "</p>" +
                "<p>로그인 후 비밀번호를 변경해 주세요.</p>" +
                "</body></html>";
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestBody @Valid PasswordChangeRequest passwordChangeRequest) {

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "resultCode", "401",
                    "message", "Access Token이 없습니다."
            ));
        }

        try {
            // Access Token으로 사용자 조회
            SiteUser user = userService.getSiteUserFromAccessToken(accessToken);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "resultCode", "401",
                        "message", "유효하지 않은 토큰입니다."
                ));
            }

            // 비밀번호 변경 요청 처리
            userService.changePassword(
                    user,
                    passwordChangeRequest.getCurrentPassword(),
                    passwordChangeRequest.getNewPassword(),
                    passwordChangeRequest.getConfirmPassword()
            );

            return ResponseEntity.ok(Map.of(
                    "resultCode", "200",
                    "message", "비밀번호가 성공적으로 변경되었습니다."
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "resultCode", "400",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "resultCode", "500",
                    "message", "서버 오류가 발생했습니다."
            ));
        }
    }


}
