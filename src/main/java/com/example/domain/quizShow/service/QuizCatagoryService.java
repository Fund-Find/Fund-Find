package com.example.domain.quizShow.service;

import com.example.domain.quizShow.entity.QuizCatagory;
import com.example.domain.quizShow.repository.QuizCatagoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizCatagoryService {
    private final QuizCatagoryRepository quizCatagoryRepository;

    public QuizCatagory write(String typeName) {
        QuizCatagory quizCatagory = QuizCatagory.builder()
                .typeName(typeName)
                .build();
        this.quizCatagoryRepository.save(quizCatagory);

        return quizCatagory;
    }
}
