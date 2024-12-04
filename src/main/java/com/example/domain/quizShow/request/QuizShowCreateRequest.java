package com.example.domain.quizShow.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
}