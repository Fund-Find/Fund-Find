package com.example.domain.quizShow.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class QuizSubmitResponse {
    private Integer score;
    private Map<Long, Boolean> results; // quizId -> isCorrect
    private Map<Long, Integer> correctAnswers; // quizId -> correctAnswerIndex
}