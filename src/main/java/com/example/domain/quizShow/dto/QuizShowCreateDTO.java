package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.constant.QuizShowImage;
import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizShowCreateDTO {
    @NotBlank
    private String showName;
    @NotNull
    private QuizShowCategoryEnum category;
    @NotBlank
    private String showDescription;
    @NotNull
    private Integer totalQuizCount;
    @NotNull
    private Integer totalScore;
//    @NotNull
    private String selectedImagePath;
    @Valid
    private List<QuizCreateDTO> quizzes;
    private boolean useCustomImage;

    // 이미지 경로 기본값 설정
    public String getEffectiveImagePath() {
        if (!useCustomImage || selectedImagePath == null || selectedImagePath.isEmpty()) {
            return QuizShowImage.getImagePathByCategory(this.category);
        }
        return selectedImagePath;
    }
}
