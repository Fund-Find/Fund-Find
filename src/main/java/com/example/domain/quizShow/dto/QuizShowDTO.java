package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<QuizDTO> quizzes;
    private String customImagePath;
    private boolean useCustomImage;
    private boolean hasVoted;
    private Long creatorId;

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
        this.customImagePath = quizShow.getCustomImagePath();
        this.useCustomImage = quizShow.isUseCustomImage();
        this.hasVoted = quizShow.isHasVoted();
        this.creatorId = quizShow.getCreator() != null ? quizShow.getCreator().getId() : null;

        if (quizShow.getQuizzes() != null) {
            this.quizzes = quizShow.getQuizzes().stream()
                    .map(QuizDTO::new)  // 수정된 QuizDTO 생성자 사용
                    .collect(Collectors.toList());
        }
    }

//    public QuizShowDTO(Long id, String showName, QuizShowCategoryEnum quizCategory, String showDescription, Integer totalQuizCount, Integer totalScore, Integer voteCount) {
//        this.id = id;
//        this.showName = showName;
//        this.quizCategory = quizCategory; // Enum 값을 직접 설정
//        this.showDescription = showDescription;
//        this.totalQuizCount = totalQuizCount;
//        this.totalScore = totalScore;
//        this.voteCount = voteCount;
//    }

}