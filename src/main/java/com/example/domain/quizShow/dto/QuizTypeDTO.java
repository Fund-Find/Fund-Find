package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizTypeDTO {
    private Long id;
    private String typeName;

    public static QuizTypeDTO from(QuizType quizType) {
        if (quizType == null) {
            return null;
        }

        return new QuizTypeDTO(
                quizType.getId(),
                quizType.getTypeName()
        );
    }
}