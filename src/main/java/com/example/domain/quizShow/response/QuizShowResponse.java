package com.example.domain.quizShow.response;

import com.example.domain.quizShow.entity.QuizShow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizShowResponse {
    private final QuizShowResponseDTO quizShow;

    public QuizShowResponse(QuizShow quizShow) {
        this.quizShow = new QuizShowResponseDTO(quizShow);
    }
}