package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.entity.QuizShow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizShowCreateResponse {
    private final QuizShowDTO quizShow;

    public QuizShowCreateResponse(QuizShow quizShow) {
        this.quizShow = new QuizShowDTO(quizShow);
    }
}