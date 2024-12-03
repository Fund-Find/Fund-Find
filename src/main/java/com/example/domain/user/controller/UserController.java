package com.example.domain.user.controller;

import com.example.domain.user.dto.request.UserRequest;
import com.example.domain.user.dto.response.UserResponse;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;
//    private JwtService jwtService;

    // 회원가입
    @GetMapping("/register")
    public String registerUser(Model model){
        model.addAttribute("userRequest", new UserRequest());
        return "user/register";
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @ModelAttribute UserRequest userRequest,
            @RequestParam("thumbnailImg") MultipartFile thumbnailImg) {
        userRequest.setThumbnailImg(thumbnailImg);
        UserResponse response = userService.registerUser(userRequest);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

//    @PostMapping("/login")
//    public ResponseEntity<TokenResponse> login(@Valid @RequestBody UserRequest request) {
//        String token = userService.authenticateAndGenerateToken(request);
//        return ResponseEntity.ok(new TokenResponse(token));
//    }


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
