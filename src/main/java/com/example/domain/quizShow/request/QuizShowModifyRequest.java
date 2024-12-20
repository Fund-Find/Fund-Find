package com.example.domain.quizShow.request;

import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizShowModifyRequest {
    @NotBlank
    private String showName;
    @NotNull
    private QuizShowCategoryEnum category;
    @NotBlank
    private String showDescription;
    @NotNull
    private Integer totalQuizCount;
    @NotNull
    private Integer totalScore;
    @Valid  // 중첩된 객체의 검증을 위해 추가
    private List<QuizRequest> quizzes;
}