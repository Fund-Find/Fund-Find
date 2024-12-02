package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import com.example.domain.quizShow.entity.QuizCategory;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.repository.QuizCategoryRepository;
import com.example.domain.quizShow.repository.QuizShowRepository;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizShowService {
    private final QuizShowRepository quizShowRepository;
    private final QuizCategoryRepository quizCategoryRepository;

    public QuizShowListResponseDTO getList(Pageable pageable) {
        Page<QuizShow> quizShowPage = this.quizShowRepository.findAll(pageable);
        return new QuizShowListResponseDTO(quizShowPage);
    }

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

    public QuizShow getQuizShow(Long id) {
        return quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈쇼를 찾을 수 없습니다."));
    }

    @Transactional
    public QuizShow create(@Valid QuizShowCreateRequest quizShowCreateRequest) {
        QuizCategory quizCategory = quizCategoryRepository.findById(quizShowCreateRequest.getQuizCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

        QuizShow quizShow = QuizShow.builder()
                .showName(quizShowCreateRequest.getShowName())
                .quizCategory(quizCategory)
                .showDescription(quizShowCreateRequest.getShowDescription())
                .totalQuizCount(quizShowCreateRequest.getTotalQuizCount())
                .totalScore(quizShowCreateRequest.getTotalScore())
                .view(0)
                .votes(new HashSet<>())
                .build();

        return quizShowRepository.save(quizShow);
    }

    @Transactional
    public QuizShow modify(Long id, QuizShowModifyRequest quizShowModifyRequest) {
        QuizShow quizShow = quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        QuizCategory quizCategory = quizCategoryRepository.findById(quizShowModifyRequest.getQuizCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

        quizShow.modify(
                quizShowModifyRequest.getShowName(),
                quizCategory,
                quizShowModifyRequest.getShowDescription(),
                quizShowModifyRequest.getTotalQuizCount(),
                quizShowModifyRequest.getTotalScore()
        );

        return quizShowRepository.save(quizShow);
    }

    @Transactional
    public QuizShowResponseDTO delete(QuizShow quizShow) {
        QuizShowResponseDTO quizShowResponseDTO = new QuizShowResponseDTO(quizShow);
        this.quizShowRepository.delete(quizShow);
        return quizShowResponseDTO;
    }
}
