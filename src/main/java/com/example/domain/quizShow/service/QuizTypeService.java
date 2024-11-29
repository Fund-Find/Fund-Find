package com.example.domain.quizShow.service;

import com.example.domain.quizShow.entity.QuizType;
import com.example.domain.quizShow.repository.QuizTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizTypeService {
    private final QuizTypeRepository quizTypeRepository;

    public QuizType write(String typeName) {
        QuizType quizType = QuizType.builder()
                .typeName(typeName)
                .build();
        this.quizTypeRepository.save(quizType);

        return quizType;
    }
}
