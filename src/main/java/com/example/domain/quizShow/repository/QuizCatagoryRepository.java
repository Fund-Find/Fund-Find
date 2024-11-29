package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.QuizCatagory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizCatagoryRepository extends JpaRepository<QuizCatagory, Long> {
}
