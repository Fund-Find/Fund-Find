package com.example.domain.quizShow.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizShowCreateRequest {
    @NotBlank
    private String showName;
    @NotNull
    private Long quizCategoryId;;
    @NotBlank
    private String showDescription;
    @NotNull
    private Integer totalQuizCount;
    @NotNull
    private Integer totalScore;
    @Valid  // 중첩된 객체의 검증을 위해 추가
    private List<QuizCreateRequest> quizzes;
}