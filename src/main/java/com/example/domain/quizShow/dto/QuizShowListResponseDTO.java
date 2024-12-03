package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowListResponseDTO {
    private List<QuizShowResponseDTO> quizShows;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;

    public QuizShowListResponseDTO(Page<QuizShow> quizShowPage) {
        this.quizShows = quizShowPage.getContent().stream()
                .map(QuizShowResponseDTO::new)  // 생성자를 사용한 변환
                .collect(Collectors.toList());
        this.totalElements = quizShowPage.getTotalElements();
        this.totalPages = quizShowPage.getTotalPages();
        this.currentPage = quizShowPage.getNumber();
    }
}