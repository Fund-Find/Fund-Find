package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import com.example.domain.quizShow.entity.QuizShow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizShowCreateResponse {
    private final QuizShowResponseDTO quizShow;

    public QuizShowCreateResponse(QuizShow quizShow) {
        this.quizShow = new QuizShowResponseDTO(quizShow);
    }
}