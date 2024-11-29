package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.QuizCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizCategoryRepository extends JpaRepository<QuizCategory, Long> {
}
