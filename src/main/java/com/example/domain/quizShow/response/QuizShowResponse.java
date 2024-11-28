package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuizShowResponse {
    private final List<QuizShowResponseDTO> quizShow;
}
