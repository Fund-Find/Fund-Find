package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizChoice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizChoiceDTO {
    private Long id;
    private Long quizId;
    private String choiceContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuizChoiceDTO(QuizChoice quizChoice) {
        this.id = quizChoice.getId();
        this.quizId = quizChoice.getQuiz().getId();
        this.choiceContent = quizChoice.getChoiceContent();
        this.createdAt = quizChoice.getCreatedDate();
        this.updatedAt = quizChoice.getModifiedDate();
    }
}