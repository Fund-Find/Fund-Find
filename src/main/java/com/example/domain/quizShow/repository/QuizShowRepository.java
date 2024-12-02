package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.QuizShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  QuizShowRepository extends JpaRepository<QuizShow, Long> {
}