package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer view = 0;
    private String customImagePath;
    private boolean useCustomImage;
    private Long creatorId;

    public QuizShowResponseDTO(QuizShow quizShow) {
        this.id = quizShow.getId();
        this.showName = quizShow.getShowName();
        this.category = quizShow.getCategory();
        this.showDescription = quizShow.getShowDescription();
        this.totalQuizCount = quizShow.getTotalQuizCount();
        this.totalScore = quizShow.getTotalScore();
        this.view = quizShow.getView();
        this.voteCount = quizShow.getVotes() != null ? quizShow.getVotes().size() : 0;
        this.createdAt = quizShow.getCreatedDate();
        this.updatedAt = quizShow.getModifiedDate();
        this.customImagePath = quizShow.getCustomImagePath();
        this.useCustomImage = quizShow.isUseCustomImage();
        this.hasVoted = quizShow.isHasVoted();
        this.creatorId = quizShow.getCreator() != null ? quizShow.getCreator().getId() : null;

        if (quizShow.getQuizzes() != null) {
            this.quizzes = quizShow.getQuizzes().stream()
                    .map(QuizResponseDTO::new)  // 수정된 QuizDTO 생성자 사용
                    .collect(Collectors.toList());
        }
    }
}









