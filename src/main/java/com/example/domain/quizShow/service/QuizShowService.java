package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.entity.*;
import com.example.domain.quizShow.repository.QuizCategoryRepository;
import com.example.domain.quizShow.repository.QuizRepository;
import com.example.domain.quizShow.repository.QuizShowRepository;
import com.example.domain.quizShow.request.QuizRequest;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.validator.QuizValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizShowService {
    private final QuizShowRepository quizShowRepository;
    private final QuizCategoryRepository quizCategoryRepository;
    private final QuizRepository quizRepository;
    private final QuizValidator quizValidator;

    public QuizShow write(String showName, String showDescription,
                          Integer totalQuizCount, Integer totalScore,
                          Integer view) {
        QuizShow quizShow = QuizShow.builder()
                .showName(showName)
                .showDescription(showDescription)
                .totalQuizCount(totalQuizCount)
                .totalScore(totalScore)
                .view(view)
                .build();
        this.quizShowRepository.save(quizShow);

        return quizShow;
    }

    public QuizShowListResponse getList(Pageable pageable) {
        Page<QuizShow> quizShowPage = this.quizShowRepository.findAll(pageable);

        List<QuizShowDTO> quizShows = quizShowPage.getContent().stream()
                .map(QuizShowDTO::new)
                .collect(Collectors.toList());

        return new QuizShowListResponse(quizShows,
                quizShowPage.getTotalElements(),
                quizShowPage.getTotalPages(),
                quizShowPage.getNumber());
    }

    public QuizShowDTO getQuizShow(Long id) {
        QuizShow quizShow = quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈쇼를 찾을 수 없습니다."));
        return new QuizShowDTO(quizShow);
    }

    @Transactional
    public QuizShowDTO create(@Valid QuizShowCreateRequest quizShowCR) {
        // 퀴즈쇼 카테고리 조회
        QuizShowCategoryEnum category = quizShowCR.getCategory();

        // QuizShow 생성
        QuizShow quizShow = QuizShow.builder()
                .showName(quizShowCR.getShowName())
                .category(quizShowCR.getCategory())
                .showDescription(quizShowCR.getShowDescription())
                .totalQuizCount(quizShowCR.getTotalQuizCount())
                .totalScore(quizShowCR.getTotalScore())
                .view(0)
                .votes(new HashSet<>())
                .build();

        quizShowRepository.save(quizShow);

        // Quiz 생성 및 저장
        if (quizShowCR.getQuizzes() != null) {
            for (QuizRequest quizReq : quizShowCR.getQuizzes()) {
                QuizShowCategory quizQuizShowCategory = quizCategoryRepository.findById(quizReq.getQuizCategoryId())
                        .orElseThrow(() -> new EntityNotFoundException("퀴즈 카테고리를 찾을 수 없습니다."));

                Quiz quiz = Quiz.builder()
                        .quizShow(quizShow)
                        .quizContent(quizReq.getQuizContent())
                        .quizScore(quizReq.getQuizScore())
                        .choices(new ArrayList<>())
                        .build();

                // 선택지 생성
                if (quizReq.getChoices() != null) {
                    for (String choiceContent : quizReq.getChoices()) {
                        QuizChoice choice = QuizChoice.builder()
                                .quiz(quiz)
                                .choiceContent(choiceContent)
                                .build();
                        quiz.getChoices().add(choice);
                    }
                }

                quizValidator.validateQuiz(quiz); // 유효성 검증
                quizRepository.save(quiz);
            }
        }

        return new QuizShowDTO(quizShow);
    }

    @Transactional
    public QuizShowDTO modify(Long id, QuizShowModifyRequest modifyRequest) {
        // 퀴즈쇼 조회
        QuizShow quizShow = quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        // 퀴즈쇼 카테고리 조회
        QuizShowCategoryEnum category = modifyRequest.getCategory();

        // 퀴즈쇼 정보 수정
        QuizShow updatedQuizShow = quizShow.toBuilder()
                .showName(modifyRequest.getShowName())
                .category(modifyRequest.getCategory())
                .showDescription(modifyRequest.getShowDescription())
                .totalQuizCount(modifyRequest.getTotalQuizCount())
                .totalScore(modifyRequest.getTotalScore())
                .build();

        quizShowRepository.save(updatedQuizShow);

        // 퀴즈 수정/추가/삭제 처리
        if (modifyRequest.getQuizzes() != null) {
            for (QuizRequest quizRequest : modifyRequest.getQuizzes()) {
                if (quizRequest.getId() == null) {
                    // 새로운 퀴즈 추가
                    createNewQuiz(updatedQuizShow, quizRequest);
                } else {
                    Quiz existingQuiz = quizRepository.findById(quizRequest.getId())
                            .orElseThrow(() -> new EntityNotFoundException("퀴즈를 찾을 수 없습니다."));

                    if (Boolean.TRUE.equals(quizRequest.getIsDeleted())) {
                        // 퀴즈 삭제
                        quizRepository.delete(existingQuiz);
                    } else {
                        // 퀴즈 수정
                        updateExistingQuiz(existingQuiz, quizRequest);
                    }
                }
            }
        }

        return new QuizShowDTO(updatedQuizShow);
    }

    private Quiz createNewQuiz(QuizShow quizShow, QuizRequest request) {
        QuizShowCategory quizShowCategory = quizCategoryRepository.findById(request.getQuizCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 카테고리를 찾을 수 없습니다."));

        Quiz quiz = Quiz.builder()
                .quizShow(quizShow)
                .quizContent(request.getQuizContent())
                .quizScore(request.getQuizScore())
                .choices(new ArrayList<>())
                .build();

        // 선택지 생성
        if (request.getChoices() != null) {
            for (String choiceContent : request.getChoices()) {
                QuizChoice choice = QuizChoice.builder()
                        .quiz(quiz)
                        .choiceContent(choiceContent)
                        .build();
                quiz.getChoices().add(choice);
            }
        }

        quizValidator.validateQuiz(quiz); // 유효성 검증
        return quizRepository.save(quiz);
    }

    private void updateExistingQuiz(Quiz quiz, QuizRequest request) {
        QuizShowCategory quizShowCategory = quizCategoryRepository.findById(request.getQuizCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 카테고리를 찾을 수 없습니다."));

        Quiz updatedQuiz = quiz.toBuilder()
                .quizContent(request.getQuizContent())
                .quizScore(request.getQuizScore())
                .build();

        // 기존 선택지 삭제 후 새로운 선택지 추가
        quiz.getChoices().clear();
        if (request.getChoices() != null) {
            for (String choiceContent : request.getChoices()) {
                QuizChoice choice = QuizChoice.builder()
                        .quiz(updatedQuiz)
                        .choiceContent(choiceContent)
                        .build();
                updatedQuiz.getChoices().add(choice);
            }
        }

        quizValidator.validateQuiz(updatedQuiz); // 유효성 검증
        quizRepository.save(updatedQuiz);
    }

    @Transactional
    public QuizShowDTO delete(Long id) {
        QuizShow quizShow = quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        // 삭제 전에 DTO 생성
        QuizShowDTO quizShowDTO = new QuizShowDTO(quizShow);

        // 퀴즈쇼 삭제 (연관된 퀴즈들도 cascade로 함께 삭제됨)
        this.quizShowRepository.delete(quizShow);

        return quizShowDTO;
    }
}