package com.example.domain.quizShow.entity;

import com.example.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuizType extends BaseEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuizTypeEnum type;
    @Column(nullable = false)
    private String typeName;
}
