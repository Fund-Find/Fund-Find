package com.example.domain.quizShow.request;

import com.example.domain.quizShow.dto.QuizChoiceCreateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class QuizRequest {
    private Long id;
    @NotNull
    private Long quizTypeId;  // QuizType 객체 대신 ID 사용
    @NotBlank
    private String quizContent;
    @NotNull
    private Integer quizScore;
    @NotNull
    @Size(min = 1)
    private List<QuizChoiceCreateDTO> choices;
    private Boolean isDeleted = false;
}