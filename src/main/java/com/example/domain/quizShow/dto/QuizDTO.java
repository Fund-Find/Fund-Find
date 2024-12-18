package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.Quiz;
import com.example.domain.quizShow.entity.QuizTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private Long id;
    private Long quizShowId;  // 퀴즈쇼 ID 추가
    private String quizContent;
    private Integer quizScore;
    private QuizTypeEnum quizType;
    private List<QuizChoiceDTO> choices;

    public QuizDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.quizShowId = quiz.getQuizShow().getId();  // 퀴즈쇼 ID 설정
        this.quizContent = quiz.getQuizContent();
        this.quizScore = quiz.getQuizScore();
        this.quizType = quiz.getQuizType().getType();
        if (quiz.getChoices() != null) {
            this.choices = quiz.getChoices().stream()
                    .map(QuizChoiceDTO::new)
                    .collect(Collectors.toList());
        }
    }
}