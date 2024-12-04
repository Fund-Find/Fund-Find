package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShowCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowCategoryDTO {
    private Long id;
    private String categoryName;

    public QuizShowCategoryDTO(QuizShowCategory quizShowCategory) {
        if (quizShowCategory != null) {
            this.id = quizShowCategory.getId();
            this.categoryName = quizShowCategory.getCategoryName();
        }
    }
}