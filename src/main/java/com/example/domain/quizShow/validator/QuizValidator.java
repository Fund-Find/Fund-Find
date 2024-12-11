package com.example.domain.quizShow.validator;

import com.example.domain.quizShow.entity.Quiz;
import com.example.domain.quizShow.entity.QuizTypeEnum;
import com.example.global.exception.QuizValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class QuizValidator {
    public void validateQuiz(Quiz quiz) {
        List<String> errors = new ArrayList<>();

        // 기본 유효성 검증
        if (quiz.getQuizScore() <= 0) {
            errors.add("퀴즈 점수는 0보다 커야 합니다.");
        }

        if (quiz.getQuizContent() == null || quiz.getQuizContent().trim().isEmpty()) {
            errors.add("퀴즈 내용은 필수입니다.");
        }

        // 퀴즈 유형별 선택지 검증
        validateChoicesByType(quiz, errors);

        if (!errors.isEmpty()) {
            throw new QuizValidationException("퀴즈 유효성 검증 실패", errors);
        }
    }

    private void validateChoicesByType(Quiz quiz, List<String> errors) {
        if (quiz.getChoices() == null || quiz.getChoices().isEmpty()) {
            errors.add("최소 하나의 선택지가 필요합니다.");
            return;
        }

        QuizTypeEnum quizType = quiz.getQuizType().getType();
        switch (quizType) {
            case MULTIPLE_CHOICE:
                validateMultipleChoice(quiz, errors);
                break;
            case TRUE_FALSE:
                validateTrueFalse(quiz, errors);
                break;
            case SUBJECTIVE:
                validateSubjective(quiz, errors);
                break;
            case SHORT_ANSWER:
                validateShortAnswer(quiz, errors);
                break;
        }
    }

    private void validateMultipleChoice(Quiz quiz, List<String> errors) {
        if (quiz.getChoices().size() < 2) {
            errors.add("객관식 문제는 최소 2개의 선택지가 필요합니다.");
        }

        boolean hasCorrectAnswer = quiz.getChoices().stream()
                .anyMatch(choice -> Boolean.TRUE.equals(choice.getIsCorrect()));

        if (!hasCorrectAnswer) {
            errors.add("적어도 하나의 정답이 지정되어야 합니다.");
        }
    }

    private void validateTrueFalse(Quiz quiz, List<String> errors) {
        if (quiz.getChoices().size() != 2) {
            errors.add("OX 문제는 정확히 2개의 선택지가 필요합니다.");
        }
    }

    private void validateSubjective(Quiz quiz, List<String> errors) {
        if (quiz.getChoices().size() < 1) {
            errors.add("주관식 문제는 최소 하나의 정답이 필요합니다.");
        }
    }

    private void validateShortAnswer(Quiz quiz, List<String> errors) {
        if (quiz.getChoices().size() < 1) {
            errors.add("단답형 문제는 정확한 답안이 필요합니다.");
        }
    }
}