package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.QuizShowCategory;
import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizShowCategoryRepository extends JpaRepository<QuizShowCategory, Long> {
    Optional<QuizShowCategory> findByCategoryName(String categoryName);
}