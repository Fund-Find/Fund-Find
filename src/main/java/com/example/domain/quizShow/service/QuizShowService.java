package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.entity.Quiz;
import com.example.domain.quizShow.entity.QuizCategory;
import com.example.domain.quizShow.entity.QuizChoice;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.repository.QuizCategoryRepository;
import com.example.domain.quizShow.repository.QuizRepository;
import com.example.domain.quizShow.repository.QuizShowRepository;
import com.example.domain.quizShow.request.QuizCreateRequest;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.response.QuizShowResponse;
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
        QuizCategory quizCategory = quizCategoryRepository.findById(quizShowCR.getQuizCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

        // QuizShow 생성
        QuizShow quizShow = QuizShow.builder()
                .showName(quizShowCR.getShowName())
                .quizCategory(quizCategory)
                .showDescription(quizShowCR.getShowDescription())
                .totalQuizCount(quizShowCR.getTotalQuizCount())
                .totalScore(quizShowCR.getTotalScore())
                .view(0)
                .votes(new HashSet<>())
                .build();

        quizShowRepository.save(quizShow);

        // Quiz 생성 및 저장
        if (quizShowCR.getQuizzes() != null) {
            for (QuizCreateRequest quizReq : quizShowCR.getQuizzes()) {
                QuizCategory quizQuizCategory = quizCategoryRepository.findById(quizReq.getQuizCategoryId())
                        .orElseThrow(() -> new EntityNotFoundException("퀴즈 카테고리를 찾을 수 없습니다."));

                Quiz quiz = Quiz.builder()
                        .quizShow(quizShow)
                        .quizCategory(quizQuizCategory)
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

                quizRepository.save(quiz);
            }
        }

        return new QuizShowDTO(quizShow);
    }

    @Transactional
    public QuizShowResponse modify(Long id, QuizShowModifyRequest quizShowMR_DTO) {
        QuizShow quizShow = quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        QuizShow updatedQuizShow = quizShow.toBuilder()
                .showName(quizShowMR_DTO.getShowName())
                .quizCategory(quizCategoryRepository.findById(quizShowMR_DTO.getQuizCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다.")))
                .showDescription(quizShowMR_DTO.getShowDescription())
                .totalQuizCount(quizShowMR_DTO.getTotalQuizCount())
                .totalScore(quizShowMR_DTO.getTotalScore())
                .build();

        return new QuizShowResponse(quizShowRepository.save(updatedQuizShow));
    }

    @Transactional
    public QuizShowResponse delete(QuizShow quizShow) {
        QuizShowResponse quizShowResponse = new QuizShowResponse(quizShow);
        this.quizShowRepository.delete(quizShow);
        return quizShowResponse;
    }
}