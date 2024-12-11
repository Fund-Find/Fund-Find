package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowDTO {
    private Long id;
    private String showName;
    private QuizShowCategoryEnum quizCategory;
    private String showDescription;
    private Integer totalQuizCount;
    private Integer totalScore;
    private Integer view;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuizShowDTO(QuizShow quizShow) {
        this.id = quizShow.getId();
        this.showName = quizShow.getShowName();
        this.quizCategory = quizShow.getCategory();
        this.showDescription = quizShow.getShowDescription();
        this.totalQuizCount = quizShow.getTotalQuizCount();
        this.totalScore = quizShow.getTotalScore();
        this.view = quizShow.getView();
        this.voteCount = quizShow.getVotes() != null ? quizShow.getVotes().size() : 0;
        this.createdAt = quizShow.getCreatedDate();
        this.updatedAt = quizShow.getModifiedDate();
    }
}