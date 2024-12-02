package com.example.domain.quizShow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowCreateRequestDTO {
    private String showName;
    private Long quizCategoryId;
    private String showDescription;
    private Integer totalQuizCount;
    private Integer totalScore;
}