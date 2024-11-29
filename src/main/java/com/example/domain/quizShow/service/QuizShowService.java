package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowCreateRequestDTO;
import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.dto.QuizShowModifyRequestDTO;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.entity.QuizCatagory;
import com.example.domain.quizShow.repository.QuizShowRepository;
import com.example.domain.quizShow.repository.QuizCatagoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class QuizShowService {
    private final QuizShowRepository quizShowRepository;
    private final QuizCatagoryRepository quizCatagoryRepository;

    public QuizShowListResponseDTO getList(Pageable pageable) {
        Page<QuizShow> quizShowPage = this.quizShowRepository.findAll(pageable);
        return QuizShowListResponseDTO.of(quizShowPage);
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

    public QuizShow create(QuizShowCreateRequestDTO quizShowCreateRequestDTO) {
        QuizCatagory quizCatagory = quizCatagoryRepository.findById(quizShowCreateRequestDTO.getQuizTypeId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

        QuizShow quizShow = QuizShow.builder()
                .showName(quizShowCreateRequestDTO.getShowName())
                .quizCatagory(quizCatagory)
                .showDescription(quizShowCreateRequestDTO.getShowDescription())
                .totalQuizCount(quizShowCreateRequestDTO.getTotalQuizCount())
                .totalScore(quizShowCreateRequestDTO.getTotalScore())
                .view(0)
                .votes(new HashSet<>())
                .build();

        return quizShowRepository.save(quizShow);
    }

    public QuizShow modify(Long id, QuizShowModifyRequestDTO quizShowModifyRequestDTO) {
        QuizShow quizShow = quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        QuizCatagory quizCatagory = quizCatagoryRepository.findById(quizShowModifyRequestDTO.getQuizTypeId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

        quizShow.modify(
                quizShowModifyRequestDTO.getShowName(),
                quizCatagory,
                quizShowModifyRequestDTO.getShowDescription(),
                quizShowModifyRequestDTO.getTotalQuizCount(),
                quizShowModifyRequestDTO.getTotalScore()
        );

        return quizShowRepository.save(quizShow);
    }

    public void delete(QuizShow quizShow) {
        this.quizShowRepository.delete(quizShow);
    }
}
