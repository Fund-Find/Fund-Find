package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerDTO {
    private Long id;
    private Long quizId;
    private Long userId;
    private String memberAnswer;
    private Boolean isCorrect;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuizAnswerDTO(QuizAnswer quizAnswer) {
        this.id = quizAnswer.getId();
        this.quizId = quizAnswer.getQuiz().getId();
        this.userId = quizAnswer.getUser().getId();
        this.memberAnswer = quizAnswer.getMemberAnswer();
        this.isCorrect = quizAnswer.getIsCorrect();
        this.answeredAt = quizAnswer.getAnsweredAt();
        this.createdAt = quizAnswer.getCreatedDate();
        this.updatedAt = quizAnswer.getModifiedDate();
    }
}