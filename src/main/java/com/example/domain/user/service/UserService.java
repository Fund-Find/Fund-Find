package com.example.domain.user.service;

import com.example.domain.user.dto.request.UserRequest;
import com.example.domain.user.dto.response.UserResponse;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.repository.UserRepository;
import com.example.global.Jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtService jwtService;


    // 회원가입
    public UserResponse registerUser(UserRequest request) {
        // 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String defaultThumbnail = request.getThumbnailImg() == null || request.getThumbnailImg().isBlank()
                ? "default_profile.png" // 기본 프로필 이미지 경로
                : request.getThumbnailImg();
        // SiteUser 엔티티 생성
        SiteUser user = SiteUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
                .email(request.getEmail())
                .nickname(request.getNickname())
                .intro(request.getIntro())
                .thumbnailImg(request.getThumbnailImg())
                .build();

        // 저장
        SiteUser savedUser = userRepository.save(user);

        // DTO 반환
        return UserResponse.fromEntity(savedUser);
    }

    public SiteUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }


    public String authenticateAndGenerateToken(UserRequest request) {
        // 사용자 확인
        SiteUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        return jwtService.generateToken(user);
    }

    public UserResponse getUserFromToken(String token) {
        if (!jwtService.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String username = jwtService.extractUsername(token);
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserResponse.fromEntity(user);
    }

    // 사용자 정보 조회
    public Optional<SiteUser> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 사용자 정보 수정
    public SiteUser updateUser(SiteUser existingUser, SiteUser updatedData) {
        if (updatedData.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }
        if (updatedData.getEmail() != null) {
            existingUser.setEmail(updatedData.getEmail());
        }
        if (updatedData.getNickname() != null) {
            existingUser.setNickname(updatedData.getNickname());
        }
        if (updatedData.getIntro() != null) {
            existingUser.setIntro(updatedData.getIntro());
        }
        if (updatedData.getName() != null) {
            existingUser.setName(updatedData.getName());
        }
        if (updatedData.getThumbnailImg() != null) {
            existingUser.setThumbnailImg(updatedData.getThumbnailImg());
        }

        return userRepository.save(existingUser);
    }

    // 사용자 삭제
    public void deleteUser(String username) {
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

}
