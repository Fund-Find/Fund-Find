package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShow;
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

    public static QuizShowModifyRequestDTO form(QuizShow quizShow) {
        return new QuizShowModifyRequestDTO(
                quizShow.getShowName(),
                quizShow.getQuizCatagory().getId(),
                quizShow.getShowDescription(),
                quizShow.getTotalQuizCount(),
                quizShow.getTotalScore()
        );
    }
}