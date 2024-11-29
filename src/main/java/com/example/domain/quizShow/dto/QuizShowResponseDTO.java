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
    private QuizTypeDTO quizType;
    private String showDescription;
    private Integer totalQuizCount;
    private Integer totalScore;
    private Integer view;
    private Integer voteCount;
    private boolean hasVoted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QuizShowResponseDTO from(QuizShow quizShow) {
        return new QuizShowResponseDTO(
                quizShow.getId(),
                quizShow.getShowName(),
                QuizTypeDTO.from(quizShow.getQuizType()),  // QuizTypeDTO에도 from 메서드 필요
                quizShow.getShowDescription(),
                quizShow.getTotalQuizCount(),
                quizShow.getTotalScore(),
                quizShow.getView(),
                quizShow.getVotes() != null ? quizShow.getVotes().size() : 0,
                false,  // hasVoted는 현재 로그인한 사용자 정보가 필요해서 별도 처리 필요
                quizShow.getCreatedDate(),
                quizShow.getModifiedDate()
        );
    }

    public QuizShowResponseDTO(QuizShow quizShow) {
        this.id = quizShow.getId();
        this.showName = quizShow.getShowName();
        this.quizType = QuizTypeDTO.from(quizShow.getQuizType());
        this.showDescription = quizShow.getShowDescription();
        this.totalQuizCount = quizShow.getTotalQuizCount();
        this.totalScore = quizShow.getTotalScore();
    }
}