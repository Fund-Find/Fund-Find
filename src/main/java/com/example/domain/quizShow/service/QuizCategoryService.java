package com.example.domain.quizShow.service;

import com.example.domain.quizShow.entity.QuizCategory;
import com.example.domain.quizShow.repository.QuizCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizCategoryService {
    private final QuizCategoryRepository quizCategoryRepository;

    public QuizCategory write(String typeName) {
        QuizCategory quizCategory = QuizCategory.builder()
                .typeName(typeName)
                .build();
        this.quizCategoryRepository.save(quizCategory);

        return quizCategory;
    }
}
