package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizShowResponse {
    private final QuizShowDTO quizShow;
}