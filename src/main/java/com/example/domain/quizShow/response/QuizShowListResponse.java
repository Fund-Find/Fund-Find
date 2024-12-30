package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowCategoryDTO;
import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.dto.QuizTypeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QuizShowListResponse {
    private List<QuizShowDTO> quizShows;
    private List<QuizTypeDTO> quizTypes;
    private List<QuizShowCategoryDTO> categories;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
}