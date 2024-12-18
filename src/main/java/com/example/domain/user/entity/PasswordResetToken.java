package com.example.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 생성
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String token;


    @OneToOne
    @NonNull
    private SiteUser user;

    private LocalDateTime expiration = LocalDateTime.now().plusHours(1); // 1시간 유효


    public PasswordResetToken(String token, SiteUser user) {
        this.token = token;
        this.user = user;
        this.expiration = LocalDateTime.now().plusHours(24); // 예: 24시간 유효
    }

    public boolean isExpired() {
        return expiration.isBefore(LocalDateTime.now());
    }
}
