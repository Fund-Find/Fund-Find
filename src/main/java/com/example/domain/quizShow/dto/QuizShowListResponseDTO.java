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
    private long totalElements;
    private int totalPages;
    private int currentPage;

    // Page<QuizShow>를 받는 생성자 추가
    public static QuizShowListResponseDTO of(Page<QuizShow> quizShowPage) {
        List<QuizShowResponseDTO> quizShowDTOs = quizShowPage.getContent().stream()
                .map(QuizShowResponseDTO::from)  // QuizShow -> QuizShowResponseDTO 변환
                .collect(Collectors.toList());

        return new QuizShowListResponseDTO(
                quizShowDTOs,
                quizShowPage.getTotalElements(),
                quizShowPage.getTotalPages(),
                quizShowPage.getNumber()
        );
    }
}