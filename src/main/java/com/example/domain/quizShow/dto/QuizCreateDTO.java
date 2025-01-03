package com.example.domain.quizShow.dto;

import com.example.domain.quizShow.constant.QuizTypeEnum;
//import com.example.domain.quizShow.entity.QuizTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizCreateDTO {
//    @NotNull
//    private Long quizTypeId;

    @NotNull(message = "퀴즈 타입은 필수입니다")
    private QuizTypeEnum quizType;
    @NotBlank(message = "퀴즈 내용은 필수입니다")
    private String quizContent;
    @NotNull(message = "퀴즈 점수는 필수입니다")
    private Integer quizScore;
    @Valid
    @NotNull(message = "선택지는 필수입니다")
    @Size(min = 2, message = "선택지는 최소 2개 이상이어야 합니다")
    private List<QuizChoiceCreateDTO> choices;
}
