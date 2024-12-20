package com.example.domain.quizShow.entity;

import com.example.domain.user.entity.SiteUser;
import com.example.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswer extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SiteUser user;

    @Column(nullable = false)
    private String userAnswer;  // OX의 경우 "T"/"F", 객관식의 경우 선택한 번호, 주관식/단답형의 경우 텍스트

    @Column(nullable = false)
    private Boolean isCorrect;  // 정답 여부 판별 결과

    private LocalDateTime answeredAt;  // 답변 시간

    public boolean validateAnswer() {
        switch(this.quiz.getQuizType().getType()) {
            case MULTIPLE_CHOICE:
                return validateMultipleChoice();
            case TRUE_FALSE:
                return validateTrueFalse();
            case SUBJECTIVE:
                return validateSubjective();
            case SHORT_ANSWER:
                return validateShortAnswer();
            default:
                return false;
        }
    }

    private boolean validateMultipleChoice() {
        // 선택지 ID로 정답 여부 확인
        return quiz.getChoices().stream()
                .filter(choice -> choice.getId().toString().equals(userAnswer))
                .findFirst()
                .map(QuizChoice::getIsCorrect)
                .orElse(false);
    }

    private boolean validateTrueFalse() {
        // 선택지 ID로 정답 여부 확인
        return quiz.getChoices().stream()
                .filter(choice -> choice.getId().toString().equals(userAnswer))
                .findFirst()
                .map(QuizChoice::getIsCorrect)
                .orElse(false);
    }

    private boolean validateSubjective() {
        // 주관식은 정답이 여러 가지일 수 있으므로 정답 목록과 비교
        return quiz.getChoices().stream()
                .anyMatch(choice -> choice.getChoiceContent().equalsIgnoreCase(userAnswer.trim()));
    }

    private boolean validateShortAnswer() {
        // 단답형은 정확히 일치해야 함 (대소문자 구분 없이, 공백 제거)
        return quiz.getChoices().stream()
                .anyMatch(choice -> choice.getChoiceContent().trim()
                        .equalsIgnoreCase(userAnswer.trim()));
    }
}