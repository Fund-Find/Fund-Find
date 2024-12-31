package com.example.domain.quizShow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuizChoiceCreateDTO {
    @NotBlank
    private String choiceContent;
    private Boolean isCorrect;
}
