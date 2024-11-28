package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.repository.QuizShowRepository;
import com.example.domain.quizShow.response.QuizShowListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizShowService {
    private final QuizShowRepository quizShowRepository;

    public List<QuizShowListResponseDTO> getList() {
        List<QuizShow> quizShowList = this.quizShowRepository.findAll();

        List<QuizShowListResponseDTO> quizShowListDTOList = quizShowList.stream()
                .map(quizShow -> new QuizShowListResponseDTO(quizShow))
                .collect(Collectors.toList());

        return quizShowListDTOList;
    }

    public QuizShow write(String showName, String showDescription,
                          Integer totalQuizCount, Integer totalScore) {
        QuizShow quizShow = QuizShow.builder()
                .showName(showName)
                .showDescription(showDescription)
                .totalQuizCount(totalQuizCount)
                .totalScore(totalScore)
                .build();
        this.quizShowRepository.save(quizShow);

        return quizShow;
    }
}
