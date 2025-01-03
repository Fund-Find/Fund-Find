package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.constant.QuizTypeEnum;
import com.example.domain.quizShow.entity.QuizType;
//import com.example.domain.quizShow.entity.QuizTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizTypeRepository extends JpaRepository<QuizType, Long> {
    Optional<QuizType> findById(Long id);
    Optional<QuizType> findByType(QuizTypeEnum type);
}