package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShow;
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
    private QuizCategoryDTO quizCategory;
    private String showDescription;
    private Integer totalQuizCount;
    private Integer totalScore;
    private Integer view;
    private Integer voteCount;
    private boolean hasVoted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuizShowResponseDTO(QuizShow quizShow) {
        this.id = quizShow.getId();
        this.showName = quizShow.getShowName();
        this.quizCategory = new QuizCategoryDTO(quizShow.getQuizCategory());
        this.showDescription = quizShow.getShowDescription();
        this.totalQuizCount = quizShow.getTotalQuizCount();
        this.totalScore = quizShow.getTotalScore();
        this.view = quizShow.getView();
        this.voteCount = quizShow.getVotes() != null ? quizShow.getVotes().size() : 0;
        this.hasVoted = false;  // 추후 구현
        this.createdAt = quizShow.getCreatedDate();
        this.updatedAt = quizShow.getModifiedDate();
    }
}