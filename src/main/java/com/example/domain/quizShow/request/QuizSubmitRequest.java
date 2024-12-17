package com.example.domain.quizShow.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmitRequest {
    @NotEmpty(message = "답안은 필수입니다.")
    private List<QuizAnswer> answers;

    @Data
    public static class QuizAnswer {
        private Long quizId;
        private Integer answer;
    }
}