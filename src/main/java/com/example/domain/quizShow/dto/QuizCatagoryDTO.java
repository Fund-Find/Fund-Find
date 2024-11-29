package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizCatagoryDTO {
    private Long id;
    private String typeName;

    public static QuizCatagoryDTO from(QuizCategory quizCategory) {
        if (quizCategory == null) {
            return null;
        }

        return new QuizCatagoryDTO(
                quizCategory.getId(),
                quizCategory.getTypeName()
        );
    }
}