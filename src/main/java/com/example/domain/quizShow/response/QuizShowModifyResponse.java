package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.entity.QuizShow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizShowModifyResponse {
    private final QuizShowDTO quizShow;

    public QuizShowModifyResponse(QuizShow quizShow) {
        this.quizShow = new QuizShowDTO(quizShow);
    }
}