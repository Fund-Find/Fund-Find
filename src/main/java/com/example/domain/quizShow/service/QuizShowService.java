package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowCreateRequestDTO;
import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.dto.QuizShowModifyRequestDTO;
import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import com.example.domain.quizShow.entity.QuizCategory;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.repository.QuizCategoryRepository;
import com.example.domain.quizShow.repository.QuizShowRepository;
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
    public QuizShowResponseDTO create(@Valid QuizShowCreateRequestDTO quizShowCR_DTO) {
        QuizCategory quizCategory = quizCategoryRepository.findById(quizShowCR_DTO.getQuizCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

        QuizShow quizShow = QuizShow.builder()
                .showName(quizShowCR_DTO.getShowName())
                .quizCategory(quizCategory)
                .showDescription(quizShowCR_DTO.getShowDescription())
                .totalQuizCount(quizShowCR_DTO.getTotalQuizCount())
                .totalScore(quizShowCR_DTO.getTotalScore())
                .view(0)
                .votes(new HashSet<>())
                .build();

        quizShowRepository.save(quizShow);

        return new QuizShowResponseDTO(quizShow);
    }

    @Transactional
    public QuizShowResponseDTO modify(Long id, QuizShowModifyRequestDTO quizShowMR_DTO) {
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

        return new QuizShowResponseDTO(quizShowRepository.save(updatedQuizShow));
    }

    @Transactional
    public QuizShowResponseDTO delete(QuizShow quizShow) {
        QuizShowResponseDTO quizShowResponseDTO = new QuizShowResponseDTO(quizShow);
        this.quizShowRepository.delete(quizShow);
        return quizShowResponseDTO;
    }
}
