package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.QuizShow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizShowRepository extends JpaRepository<QuizShow, Long> {
    @Query("SELECT qs FROM QuizShow qs WHERE qs.id = :id")
    Optional<QuizShow> findQuizShowById(@Param("id") Long id);

    @Query("SELECT qs FROM QuizShow qs LEFT JOIN FETCH qs.quizzes WHERE qs.id = :id")
    Optional<QuizShow> findByIdWithQuizzes(@Param("id") Long id);

    // FETCH JOIN과 페이징을 함께 사용하기 위한 수정된 쿼리
    @Query(value = "SELECT DISTINCT qs FROM QuizShow qs " +
            "LEFT JOIN qs.quizzes q " +
            "LEFT JOIN q.choices " +
            "ORDER BY qs.id DESC",  // id로 정렬 추가
            countQuery = "SELECT COUNT(DISTINCT qs) FROM QuizShow qs")
    Page<QuizShow> findAllWithQuizzesAndChoices(Pageable pageable);
}