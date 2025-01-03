package com.example.domain.quizShow.dto;

import lombok.*;

import java.util.List;

//문제 출제용
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizChoiceResponseDTO {
    private Long id;
    private Long quizId;
    private String choiceContent;
}