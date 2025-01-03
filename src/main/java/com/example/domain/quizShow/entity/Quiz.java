package com.example.domain.quizShow.entity;

import com.example.global.jpa.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Quiz extends BaseEntity {
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private QuizShow quizShow;

    @Column(columnDefinition = "VARCHAR(1000)", nullable = false)
    private String quizContent;

    @Column(nullable = false)
    private Integer quizScore;

    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @BatchSize(size = 100)
    @Builder.Default // 기본값 유지
    @OrderBy("id ASC")  // id 기준으로 정렬
    private List<QuizChoice> choices = new ArrayList<>(); // NPE 방지를 위한 초기화

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private QuizType quizType;

    public void setChoices(List<QuizChoice> newChoices) {
        // 기존 choices와의 관계를 안전하게 제거
        if (this.choices != null) {
            this.choices.forEach(choice -> choice.setQuiz(null));
            this.choices.clear();
        }

        if (newChoices != null) {
            newChoices.forEach(choice -> {
                this.choices.add(choice);
                choice.setQuiz(this);  // 양방향 관계 설정
            });
        }
    }

    public void addChoice(QuizChoice choice) {
        if (choice != null) {
            if (this.choices == null) {
                this.choices = new ArrayList<>();
            }
            this.choices.add(choice);
            choice.setQuiz(this);  // 양방향 관계 설정
        }
    }

    public void setQuizShow(QuizShow quizShow) {
        this.quizShow = quizShow;
    }

    public void addChoices(List<QuizChoice> choices) {
        this.choices = choices;
        if (choices != null) {
            choices.forEach(choice -> choice.setQuiz(this));
        }
    }
}