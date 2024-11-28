package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class QuizShowListResponse {
    private final List<QuizShowListResponseDTO> quizShowList;
}
