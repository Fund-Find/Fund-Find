package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.entity.QuizShowCategory;
import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizShowCategoryDTO {
    private Long id;
    private String categoryName;
    private String description;  // 카테고리 설명
    private QuizShowCategoryEnum categoryEnum;

    public QuizShowCategoryDTO(QuizShowCategory category) {
        this.id = category.getId();
        this.categoryName = category.getCategoryName();
        // Enum에서 해당하는 카테고리를 찾아 description 설정
        this.categoryEnum = Arrays.stream(QuizShowCategoryEnum.values())
                .filter(enumValue -> enumValue.getDescription().equals(category.getCategoryName()))
                .findFirst()
                .orElse(null);
        this.description = this.categoryEnum != null ? this.categoryEnum.getDescription() : "";
    }

    // Enum으로부터 직접 DTO 생성하는 정적 메소드
    public static QuizShowCategoryDTO fromEnum(QuizShowCategoryEnum categoryEnum) {
        return new QuizShowCategoryDTO(
                null, // ID는 null로 설정 (Enum 기반 생성시)
                categoryEnum.name(),
                categoryEnum.getDescription(),
                categoryEnum
        );
    }
}
