package com.example.domain.quizShow.service;

import com.example.domain.quizShow.entity.QuizShowCategory;
import com.example.domain.quizShow.repository.QuizCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizCategoryService {
    private final QuizCategoryRepository quizCategoryRepository;

    public QuizShowCategory write(String categoryName) {
        QuizShowCategory quizShowCategory = QuizShowCategory.builder()
                .categoryName(categoryName)
                .build();
        this.quizCategoryRepository.save(quizShowCategory);

        return quizShowCategory;
    }
}