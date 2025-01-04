package com.example.domain.quizShow.entity;

import com.example.domain.quizShow.constant.QuizShowImage;
import com.example.domain.user.entity.SiteUser;
import com.example.global.jpa.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
public class QuizShow extends BaseEntity {
    @Column(nullable = false)
    private String showName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizShowCategoryEnum category;

    private String showDescription;

    @Column(nullable = false)
    private Integer totalQuizCount;

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false)
    private Integer view = 0; // 기본값 0으로 초기화

    // 기본 생성자
    public QuizShow() {
        this.view = 0;
    }
    private LocalDateTime lastViewedAt;

    @ManyToMany
    @JoinTable(
            name = "quiz_show_votes",
            joinColumns = @JoinColumn(name = "quiz_show_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    @JsonManagedReference
    private Set<SiteUser> votes = new HashSet<>();

    @OneToMany(mappedBy = "quizShow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @BatchSize(size = 100)
    @JsonManagedReference
    private List<Quiz> quizzes;

    @Transient
    private boolean hasVoted = false;

    public boolean hasVoted(Long userId) {
        return this.votes.stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    public QuizShow updateVoteStatus(SiteUser user) {
        boolean newVoteStatus = toggleVote(user);
        return this.toBuilder()
                .hasVoted(newVoteStatus)
                .build();
    }

    private boolean toggleVote(SiteUser user) {
        if (votes.contains(user)) {
            votes.remove(user);
            return false;
        } else {
            votes.add(user);
            return true;
        }
    }

    public boolean checkUserVoted(Long userId) {
        return this.votes.stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    @Column
    private String customImagePath; // 사용자 지정 이미지 경로

    @Column
    private boolean useCustomImage; // 사용자 이미지 사용 여부

    @Transient
    private String effectiveImagePath; // 실제 사용될 이미지 경로 (DB에 저장되지 않음)

    public String getEffectiveImagePath() {
        if (useCustomImage && customImagePath != null && !customImagePath.isEmpty()) {
            return customImagePath;
        }
        return QuizShowImage.getImagePathByCategory(this.category);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnore
    private SiteUser creator;  // 작성자 정보 추가

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
        if (quizzes != null) {
            quizzes.forEach(quiz -> quiz.setQuizShow(this));
        }
    }
}