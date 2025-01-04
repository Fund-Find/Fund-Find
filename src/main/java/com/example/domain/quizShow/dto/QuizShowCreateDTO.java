package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.constant.QuizShowImage;
import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String customImagePath;
    @Valid
    private List<QuizCreateDTO> quizzes;
    private boolean useCustomImage;

    // 이미지 경로 기본값 설정
    public String getEffectiveImagePath() {
        if (!useCustomImage || customImagePath == null || customImagePath.isEmpty()) {
            return QuizShowImage.getImagePathByCategory(this.category);
        }
        return customImagePath;
    }
    @JsonIgnore
    private MultipartFile imageFile; // 이미지 경로
    @JsonIgnore
    public boolean isUsingFileUpload() {
        return imageFile != null && !imageFile.isEmpty();
    }
}