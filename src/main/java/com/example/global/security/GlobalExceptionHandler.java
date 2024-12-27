package com.example.global.security;

import com.example.global.rsData.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<RsData<Object>> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(RsData.of("401", "로그인이 필요한 서비스입니다.", null));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("code", "401");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        // 첫 번째 필드 에러 가져오기
        FieldError firstError = ex.getBindingResult().getFieldErrors()
                .stream()
                .findFirst() // 첫 번째 에러만 가져오기
                .orElse(null); // 에러가 없으면 null

        // 응답 메시지 구성
        Map<String, String> response = new HashMap<>();
        if (firstError != null) {
            response.put("field", firstError.getField()); // 에러 필드 이름
            response.put("msg", firstError.getDefaultMessage()); // 에러 메시지
        } else {
            response.put("msg", "알 수 없는 오류가 발생했습니다."); // 에러가 없을 때 기본 메시지
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 오류가 발생했습니다.");
    }
}
