package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QuizShowListResponse {
    private List<QuizShowDTO> quizShows;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
}