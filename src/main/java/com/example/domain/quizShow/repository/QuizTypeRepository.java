package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizTypeRepository extends JpaRepository<QuizType, Long> {
}
