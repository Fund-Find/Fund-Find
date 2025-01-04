package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.Quiz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {
    private Long id;
    private Long quizShowId;  // 퀴즈쇼 ID 추가
    private Long quizTypeId;
    private String quizContent;
    private Integer quizScore;
    private List<QuizChoiceDTO> choices;

    public QuizResponseDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.quizShowId = quiz.getQuizShow().getId();  // 퀴즈쇼 ID 설정
        this.quizContent = quiz.getQuizContent();
        this.quizScore = quiz.getQuizScore();
        this.quizTypeId = quiz.getQuizType().getId();
        if (quiz.getChoices() != null) {
            this.choices = quiz.getChoices().stream()
                    .map(QuizChoiceDTO::new)
                    .collect(Collectors.toList());
        }
    }
}
