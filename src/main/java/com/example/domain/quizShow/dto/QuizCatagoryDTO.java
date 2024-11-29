package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizCatagory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizCatagoryDTO {
    private Long id;
    private String typeName;

    public static QuizCatagoryDTO from(QuizCatagory quizCatagory) {
        if (quizCatagory == null) {
            return null;
        }

        return new QuizCatagoryDTO(
                quizCatagory.getId(),
                quizCatagory.getTypeName()
        );
    }
}