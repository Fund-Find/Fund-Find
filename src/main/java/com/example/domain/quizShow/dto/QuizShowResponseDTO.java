package com.example.domain.quizShow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowResponseDTO {
    private Long id;
    private String showName;
    private QuizTypeDTO quizType;
    private String showDescription;
    private Integer totalQuizCount;
    private Integer totalScore;
    private Integer view;
    private Integer voteCount;
    private boolean hasVoted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}