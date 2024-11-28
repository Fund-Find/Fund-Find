package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.repository.QuizShowRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizShowService {
    private final QuizShowRepository quizShowRepository;

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
}
