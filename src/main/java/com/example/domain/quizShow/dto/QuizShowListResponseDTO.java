package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowListResponseDTO {
    private List<QuizShowResponseDTO> quizShows;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    public QuizShowListResponseDTO(QuizShow quizShow) {
    }
}