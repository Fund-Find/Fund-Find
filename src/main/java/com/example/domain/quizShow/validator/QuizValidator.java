package com.example.domain.quizShow.validator;

import com.example.domain.quizShow.constant.QuizTypeEnum;
import com.example.domain.quizShow.entity.Quiz;
import com.example.domain.quizShow.entity.QuizType;
//import com.example.domain.quizShow.entity.QuizTypeEnum;
import com.example.domain.quizShow.repository.QuizTypeRepository;
import com.example.global.exception.QuizValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuizValidator {
    // QuizType 엔티티를 조회하기 위한 레포지토리
    private final QuizTypeRepository quizTypeRepository;

    /**
     * 퀴즈 유효성 검증
     * @param quiz 검증할 퀴즈
     * @param quizTypeId 퀴즈 타입 ID
     */
    public void validateQuiz(Quiz quiz, Long quizTypeId) {
        List<String> errors = new ArrayList<>();

        // 기본 유효성 검증
        if (quiz.getQuizScore() <= 0) {
            errors.add("퀴즈 점수는 0보다 커야 합니다.");
        }

        if (quiz.getQuizContent() == null || quiz.getQuizContent().trim().isEmpty()) {
            errors.add("퀴즈 내용은 필수입니다.");
        }

        // 퀴즈 타입 조회 - ID로 QuizType 엔티티 조회
        QuizType quizType = quizTypeRepository.findById(quizTypeId)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));


        // 퀴즈 유형별 선택지 검증 - QuizType의 type을 직접 전달
        validateChoicesByType(quiz, quizType.getType(), errors);

        // 에러가 있다면 예외 발생
        if (!errors.isEmpty()) {
            throw new QuizValidationException("퀴즈 유효성 검증 실패", errors);
        }
    }

    /**
     * 퀴즈 유형별 선택지 검증
     * @param quiz 검증할 퀴즈
     * @param quizType 퀴즈 타입
     * @param errors 에러 메시지 리스트
     */
    private void validateChoicesByType(Quiz quiz, QuizTypeEnum quizType, List<String> errors) {
        if (quiz.getChoices() == null || quiz.getChoices().isEmpty()) {
            errors.add("최소 하나의 선택지가 필요합니다.");
            return;
        }

        // 퀴즈 타입별 검증 로직 실행
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

    /**
     * 객관식 문제 검증
     * @param quiz 검증할 퀴즈
     * @param errors 에러 메시지 리스트
     */
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

    /**
     * OX 문제 검증
     * @param quiz 검증할 퀴즈
     * @param errors 에러 메시지 리스트
     */
    private void validateTrueFalse(Quiz quiz, List<String> errors) {
        if (quiz.getChoices().size() != 2) {
            errors.add("OX 문제는 정확히 2개의 선택지가 필요합니다.");
        }
    }

    /**
     * 주관식 문제 검증
     * @param quiz 검증할 퀴즈
     * @param errors 에러 메시지 리스트
     */
    private void validateSubjective(Quiz quiz, List<String> errors) {
        if (quiz.getChoices().isEmpty()) {
            errors.add("주관식 문제는 최소 하나의 정답이 필요합니다.");
        }
    }

    /**
     * 단답형 문제 검증
     * @param quiz 검증할 퀴즈
     * @param errors 에러 메시지 리스트
     */
    private void validateShortAnswer(Quiz quiz, List<String> errors) {
        if (quiz.getChoices().isEmpty()) {
            errors.add("단답형 문제는 정확한 답안이 필요합니다.");
        }
    }
}