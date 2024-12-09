package com.example.domain.quizShow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

//문제 출제용
@Getter
@AllArgsConstructor
public class QuizChoiceResponseDTO {
    private Long id;
    private Long quizId;
    private String choiceContent;
}