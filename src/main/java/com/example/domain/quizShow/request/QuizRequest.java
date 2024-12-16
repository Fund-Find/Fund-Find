package com.example.domain.quizShow.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class QuizRequest {
    private Long id;
    @NotNull
    private Long quizTypeId;  // QuizType 객체 대신 ID 사용
    @NotBlank
    private String quizContent;
    @NotNull
    private Integer quizScore;
    @NotNull
    @Size(min = 2, message = "선택지는 최소 2개 이상이어야 합니다")
    private List<String> choices;
    private Boolean isDeleted = false;
}