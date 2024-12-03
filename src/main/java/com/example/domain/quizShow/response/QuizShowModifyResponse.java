package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import com.example.domain.quizShow.entity.QuizShow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizShowModifyResponse {
    private final QuizShowResponseDTO quizShow;

    public QuizShowModifyResponse(QuizShow quizShow) {
        this.quizShow = new QuizShowResponseDTO(quizShow);
    }
}