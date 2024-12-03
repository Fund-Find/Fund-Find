package com.example.domain.quizShow.entity;

import com.example.domain.user.entity.SiteUser;
import com.example.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

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
    @JoinColumn(name = "quiz_category_id")
    private QuizCategory quizCategory;

    private String showDescription;

    @Column(nullable = false)
    private Integer totalQuizCount;

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false, columnDefinition = "integer default 0")
    @ColumnDefault("0")
    private Integer view;

    @ManyToMany
    @ColumnDefault("0")
    @JoinTable(
            name = "quiz_show_votes",
            joinColumns = @JoinColumn(name = "quiz_show_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<SiteUser> votes;
}