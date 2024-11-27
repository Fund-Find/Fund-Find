package com.example.domain.quizShow.entity;

import com.example.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuizShow extends BaseEntity {
    @Column(nullable = false)
    private String showName;

    @ManyToOne(fetch = FetchType.LAZY)  // QuizType도 엔티티이므로 관계 매핑 필요
    @JoinColumn(name = "quiz_type_id")
    private QuizType quizType;

    private String showDescription;

    @Column(nullable = false)
    private Integer totalQuizCount;

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false)
    private Integer view;

    @ManyToMany
    @JoinTable(
            name = "quiz_show_votes",
            joinColumns = @JoinColumn(name = "quiz_show_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> votes;
}