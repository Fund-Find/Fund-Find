package com.example.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String email, String token) {
        String resetLink = "http://localhost:8080/api/v1/user/reset-password/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("비밀번호 재발급 요청");
        message.setText("비밀번호 재발급을 요청하셨습니다.\n아래 링크를 클릭하여 비밀번호를 받아보세요:\n" + resetLink);

        mailSender.send(message);
    }
}
