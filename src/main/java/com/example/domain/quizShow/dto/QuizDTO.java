package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizChoice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private Long quizShowId;
    private QuizTypeDTO quizType;
    private String quizContent;
    private Integer quizScore;
    private List<QuizChoice> choices;
}