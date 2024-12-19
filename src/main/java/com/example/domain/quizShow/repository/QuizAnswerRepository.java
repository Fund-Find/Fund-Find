package com.example.domain.quizShow.repository;

import com.example.domain.quizShow.entity.QuizAnswer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.user.id = :userId ORDER BY qa.answeredAt DESC")
    List<QuizAnswer> findLatestByUserId(@Param("userId") Long userId, Pageable pageable);

    // 최신 답안 외 삭제
    @Modifying
    @Query("""
        DELETE FROM QuizAnswer qa 
        WHERE qa.user.id = :userId 
        AND qa.id NOT IN (
            SELECT qa2.id FROM QuizAnswer qa2 
            WHERE qa2.user.id = :userId 
            ORDER BY qa2.answeredAt DESC 
            LIMIT :limit
        )
    """)
    void deleteOldAnswers(@Param("userId") Long userId, @Param("limit") int limit);
}