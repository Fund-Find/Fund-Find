package com.example.domain.quizShow.request;

import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class QuizShowCreateRequest {
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
    @NotNull
    private String customImagePath;
    @Valid  // 중첩된 객체의 검증을 위해 추가
    private List<QuizRequest> quizzes;
    @JsonIgnore
    private MultipartFile imageFile; // 이미지 경로
    private boolean useCustomImage; // 사용자 이미지 사용 여부
    @JsonIgnore
    public boolean isUsingFileUpload() {
        return imageFile != null && !imageFile.isEmpty();
    }
}