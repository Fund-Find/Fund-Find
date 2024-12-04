package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizCreateResponse {
    private final QuizDTO quiz;
}
