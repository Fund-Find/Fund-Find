package com.example.domain.quizShow.request;

import com.example.domain.quizShow.entity.QuizTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmitRequest {
    @NotEmpty(message = "답안은 필수입니다.")
    private List<QuizAnswer> answers;

    @Data
    public static class QuizAnswer {
        @NotNull(message = "퀴즈 ID는 필수입니다.")
        private Long quizId;

        // 객관식/OX 문제용 인덱스
        private Integer choiceIndex;

        // 주관식/단답형 문제용 텍스트
        private String textAnswer;

        // 퀴즈 타입
        @NotNull(message = "퀴즈 타입은 필수입니다.")
        private QuizTypeEnum quizType;

        // 답안 타입에 따라 적절한 값 반환
        public String getFormattedAnswer() {
            switch (quizType) {
                case MULTIPLE_CHOICE:
                    return String.valueOf(choiceIndex);
                case TRUE_FALSE:
                    return choiceIndex == 0 ? "T" : "F";
                case SUBJECTIVE:
                case SHORT_ANSWER:
                    return textAnswer;
                default:
                    throw new IllegalArgumentException("지원하지 않는 퀴즈 타입입니다.");
            }
        }
    }
}