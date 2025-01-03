package com.example.domain.quizShow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
public class QuizChoiceCreateDTO {
    @NotBlank
    private String choiceContent;
    private Boolean isCorrect;
}
