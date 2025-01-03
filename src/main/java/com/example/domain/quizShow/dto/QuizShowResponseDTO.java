package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowResponseDTO {
    private Long id;
    private String showName;
    private QuizShowCategoryEnum category;
    private String showDescription;
    private Integer totalQuizCount;
    private Integer totalScore;
    private String effectiveImagePath;
    private List<QuizResponseDTO> quizzes;
    private boolean hasVoted;
    private int voteCount;
    private Integer view = 0;
}
