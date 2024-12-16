package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q " +
            "LEFT JOIN FETCH q.choices " +
            "WHERE q.id = :id")
    Optional<Quiz> findByIdWithChoices(@Param("id") Long id);

    @Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.choices WHERE q.quizShow.id = :quizShowId")
    List<Quiz> findQuizzesByQuizShowId(@Param("quizShowId") Long quizShowId);
}