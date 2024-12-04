package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
