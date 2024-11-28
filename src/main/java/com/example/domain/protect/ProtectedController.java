//package com.example.domain.protect;
//
//import com.example.global.Jwt.JwtService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/protected")
//@RequiredArgsConstructor
//public class ProtectedController {
//
//    private final JwtService jwtService;
//
//    @GetMapping("/data")
//    public ResponseEntity<String> getProtectedData(@RequestHeader("Authorization") String authorizationHeader) {
//        String token = authorizationHeader.replace("Bearer ", "");
//
//        if (!jwtService.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다."); // 인증 실패
//        }
//
//        return ResponseEntity.ok("보호된 데이터입니다."); // 인증 성공
//    }
//}
