package com.example.domain.quizShow.service;

import com.example.domain.quizShow.entity.QuizShowCategory;
import com.example.domain.quizShow.repository.QuizShowCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizCategoryService {
    private final QuizShowCategoryRepository quizCategoryRepository;

    public QuizShowCategory write(String categoryName) {
        QuizShowCategory quizShowCategory = QuizShowCategory.builder()
                .categoryName(categoryName)
                .build();
        this.quizCategoryRepository.save(quizShowCategory);

        return quizShowCategory;
    }
}