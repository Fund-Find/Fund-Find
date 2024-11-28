package com.example.domain.quizShow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowModifyRequestDTO {
    private String showName;
    private Long quizTypeId;
    private String showDescription;
    private Integer totalQuizCount;
    private Integer totalScore;
}