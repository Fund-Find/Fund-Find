package com.example.domain.quizShow.entity;

import com.example.domain.global.jpa.BaseEntity;
import com.example.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswer extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String userAnswer;      // 사용자가 입력한 답변

    @Column(nullable = false)
    private Boolean isCorrect;      // 정답 여부 판별 결과

    // 필요에 따라 추가될 수 있는 필드
    private LocalDateTime answeredAt;  // 답변 시간
}