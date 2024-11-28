package com.example.domain.quizShow.response;

import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuizShowListResponse {
    private final QuizShowListResponseDTO quizShowList;  // List 대신 단일 DTO로 변경
}