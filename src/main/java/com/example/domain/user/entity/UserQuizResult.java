package com.example.domain.user.entity;

import com.example.global.jpa.BaseEntity;
import com.example.domain.quizShow.entity.QuizShow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserQuizResult extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_show_id", nullable = false)
    private QuizShow quizShow;

    @Column(nullable = false)
    private Integer score;
}