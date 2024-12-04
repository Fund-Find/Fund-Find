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
    private Boolean isCorrect;        // 추가: 정답 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuizChoiceDTO(QuizChoice quizChoice) {
        this.id = quizChoice.getId();
        this.quizId = quizChoice.getQuiz().getId();
        this.choiceContent = quizChoice.getChoiceContent();
        this.isCorrect = quizChoice.getIsCorrect();  // 추가
        this.createdAt = quizChoice.getCreatedDate();
        this.updatedAt = quizChoice.getModifiedDate();
    }

    // 문제 출제용 DTO 반환 (정답 정보 제외)
    public QuizChoiceResponseDTO toResponseDTO() {
        return new QuizChoiceResponseDTO(id, quizId, choiceContent);
    }
}