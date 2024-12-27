package com.example.domain.user.repository;

import com.example.domain.user.entity.PasswordResetToken;
import com.example.domain.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token); // 토큰으로 검색

    Optional<PasswordResetToken> findByUser(SiteUser user);
}
