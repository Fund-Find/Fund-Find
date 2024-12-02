package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizCategoryDTO {
    private Long id;
    private String categoryName;

    public QuizCategoryDTO(QuizCategory quizCategory) {
        if (quizCategory != null) {
            this.id = quizCategory.getId();
            this.categoryName = quizCategory.getCategoryName();
        }
    }
}