package com.example.domain.user.repository;

import com.example.domain.user.entity.UserQuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuizResultRepository extends JpaRepository<UserQuizResult, Long> {
    List<UserQuizResult> findByUserId(Long userId);
}