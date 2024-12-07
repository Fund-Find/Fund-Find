package com.example.domain.user.controller;

import com.example.domain.user.dto.request.UserRequest;
import com.example.domain.user.dto.response.UserResponse;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import com.example.global.Jwt.JwtProvider;
import com.example.global.RsData.RsData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
            @RequestPart("thumbnailImg") MultipartFile thumbnailImg) {  // 파일 업로드 처리
        userRequest.setThumbnailImg(thumbnailImg);  // 파일을 UserRequest에 설정
        System.out.println("test");

        SiteUser user = this.userService.registerUser(userRequest);
        return RsData.of("200", "회원가입이 완료되었습니다.", new UserResponse(user));
    }

    @PostMapping("/login")
    public RsData<UserResponse> login (@Valid @RequestBody UserRequest Request, HttpServletResponse res) {

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



        return RsData.of("200", "토큰 발급 성공: " + accessToken , new UserResponse(user));
    }

 
//        // accessToken 발급
//        String accessToken = jwtProvider.genAccessToken(user);
//        Cookie cookie = new Cookie("accessToken", accessToken);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);

//
//        response.addCookie(cookie);
//        return RsData.of("200", "토큰 발급 성공" + accessToken, new MemberResponse(member));

//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
//        String token = authorizationHeader.replace("Bearer ", "");
//        UserResponse user = userService.getUserFromToken(token);
//        return ResponseEntity.ok(user);
//    }


    // 사용자 정보 조회
    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        return userService.findUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 사용자 정보 수정
    @PatchMapping("/{username}")
    public ResponseEntity<?> updateUser(
            @PathVariable String username,
            @RequestBody UserRequest updatedData) {

        SiteUser existingUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 업데이트 메서드 호출
        UserResponse updatedUserResponse = userService.updateUser(existingUser, updatedData);

        return ResponseEntity.ok(updatedUserResponse);
    }


    // 사용자 삭제
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("사용자가 삭제되었습니다.");
    }
}
