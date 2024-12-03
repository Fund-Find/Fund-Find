package com.example.domain.quizShow.entity;

import com.example.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Quiz extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private QuizShow quizShow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private QuizType quizType;

    @Column(columnDefinition = "VARCHAR(1000)", nullable = false)
    private String quizContent;

    @Column(nullable = false)
    private Integer quizScore;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizChoice> choices;
}