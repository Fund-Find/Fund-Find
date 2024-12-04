package com.example.domain.quizShow.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizCreateRequest {
    @NotNull
    private Long quizCategoryId;

    @NotBlank
    private String quizContent;

    @NotNull
    private Integer quizScore;

    private List<String> choices;
}
