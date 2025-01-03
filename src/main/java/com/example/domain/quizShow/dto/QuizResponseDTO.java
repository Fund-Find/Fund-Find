package com.example.domain.quizShow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {
    private Long id;
    private Long quizTypeId;
    private String quizContent;
    private Integer quizScore;
    private List<QuizChoiceResponseDTO> choices;
}
