package com.example.domain.user.service;

import com.example.domain.user.dto.request.UserPatchRequest;
import com.example.domain.user.dto.request.UserRequest;
import com.example.domain.user.dto.response.UserResponse;
import com.example.domain.user.entity.PasswordResetToken;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.repository.PasswordResetTokenRepository;
import com.example.domain.user.repository.UserRepository;
import com.example.global.Jwt.JwtProvider;
import com.example.global.rsData.RsData;
import com.example.global.security.SecurityUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final JwtProvider jwtProvider;


    // 회원가입
    public SiteUser registerUser(UserRequest request) {
        // 비밀번호와 비밀번호 확인란 비교
        if (!request.getPassword1().equals(request.getPassword2())) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }

        // 중복 확인
        if (this.userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 프로필 이미지 처리
        String profileImageUrl = "http://localhost:8080/img/login-icon.svg"; // 기본 프로필 이미지 경로
        if (request.getThumbnailImg() != null && !request.getThumbnailImg().isEmpty()) {
            profileImageUrl = fileStorageService.storeFile(request.getThumbnailImg());
        }
        // SiteUser 엔티티 생성
        SiteUser user = SiteUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword1())) // 비밀번호 암호화
                .email(request.getEmail())
                .nickname(request.getNickname())
                .intro(request.getIntro())
                .thumbnailImg(profileImageUrl)
                .build();
        String refreshToken = jwtProvider.genRefreshToken(user);
        user.setRefreshToken(refreshToken);

        // 저장
        SiteUser savedUser = this.userRepository.save(user);

        return savedUser;
    }

    public RsData<String> refreshAccessToken(String refreshToken) {
        if (!jwtProvider.verify(refreshToken)) {
            return RsData.of("403", "Refresh Token이 유효하지 않습니다.");
        }

        try {
            String newAccessToken = jwtProvider.refreshAccessToken(refreshToken);
            return RsData.of("200", "Access Token 재발급 성공", newAccessToken);
        } catch (Exception e) {
            log.error("Refresh Token으로 Access Token 재발급 실패: {}", e.getMessage());
            return RsData.of("500", "Access Token 재발급 실패");
        }
    }

    public UserResponse updateUser(SiteUser existingUser, UserPatchRequest updatedData) {
        if (updatedData.getNickname() != null) {
            existingUser.setNickname(updatedData.getNickname());
        }
        if (updatedData.getIntro() != null) {
            existingUser.setIntro(updatedData.getIntro());
        }
        // 이미지 변경 처리
        if (updatedData.getThumbnailImg() != null && !updatedData.getThumbnailImg().isEmpty()) {
            if (!existingUser.getThumbnailImg().equals("http://localhost:8080/img/login-icon.svg")) {
                fileStorageService.deleteFile(existingUser.getThumbnailImg());
            }
            String newImageUrl = fileStorageService.storeFile(updatedData.getThumbnailImg());
            System.out.println("이미지 :http://localhost:8080/uploads/" + newImageUrl);
            existingUser.setThumbnailImg("http://localhost:8080/uploads/" + newImageUrl.substring(newImageUrl.lastIndexOf("\\") + 1)); //
        }

        SiteUser updatedUser = userRepository.save(existingUser);
        return UserResponse.fromEntity(updatedUser);
    }


    public SiteUser getUser(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public SiteUser getUser(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 사용자 정보 조회
    public Optional<SiteUser> findUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }


    // 사용자 삭제
    public void deleteUser(String username) {
        SiteUser user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        this.userRepository.delete(user);
    }

    public boolean validateToken(String accessToken) {
        return jwtProvider.verify(accessToken);
    }

    public SecurityUser getUserFromAccessToken(String accessToken) {
        Map<String, Object> payloadBody = jwtProvider.getClaims(accessToken);

        long id = (int) payloadBody.get("id");
        String username = (String) payloadBody.get("username");
        List<GrantedAuthority> authorities = new ArrayList<>();

        return new SecurityUser(id, username, "", authorities);
    }

    public SiteUser getSiteUserFromAccessToken(String accessToken) {
        // JWT 토큰에서 클레임 추출
        Map<String, Object> payloadBody = jwtProvider.getClaims(accessToken);

        // 사용자 이름을 토큰에서 추출
        String username = (String) payloadBody.get("username");

        // 데이터베이스에서 사용자 조회
        SiteUser siteUser = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String baseUrl = "http://localhost:8080/uploads/"; // 업로드 이미지 파일이 제공되는 서버 URL
        String thumbnailPath = siteUser.getThumbnailImg();

        String thumbnailUrl = thumbnailPath.startsWith("C:") // 파일 시스템 경로인지 확인
                ? baseUrl + thumbnailPath.substring(thumbnailPath.lastIndexOf("\\") + 1) // 파일명만 추출하여 URL 생성
                : thumbnailPath; // 이미 URL 형식이면 그대로 사용

        siteUser.setThumbnailImg(thumbnailUrl);


        // SiteUser 객체 반환
        return siteUser;
    }

    public SiteUser getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null); // 유저가 없으면 null 반환
    }

    public SiteUser findByUsernameAndEmail(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email).orElse(null);
    }


    private final PasswordResetTokenRepository tokenRepository;

    // 비밀번호 재설정 토큰 생성
    public String createPasswordResetToken(SiteUser user) {
        // 기존 토큰이 있는지 확인
        Optional<PasswordResetToken> existingToken = this.tokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            // 기존 토큰 삭제
            tokenRepository.delete(existingToken.get());

            log.info("기존 비밀번호 재설정 토큰을 삭제했습니다. 사용자 ID: {}", user.getId());
            System.out.println("있어선 안될 값 : " + tokenRepository.findByUser(user));
        } else {
            log.info("기존 비밀번호 재설정 토큰이 존재하지 않습니다. 사용자 ID: {}", user.getId());
        }

        // 새로운 토큰 생성 및 저장
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);
        log.info("새로운 비밀번호 재설정 토큰을 생성했습니다. 사용자 ID: {}", user.getId());
        return token;
    }

    public SiteUser verifyPasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElse(null);
        if (resetToken == null || resetToken.isExpired()) {
            return null;
        }
        return resetToken.getUser();
    }

    public String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8); // 8자리 랜덤 비밀번호
    }

    public void updatePassword(SiteUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    @Transactional
    public boolean changePassword(SiteUser user, String currentPassword, String newPassword, String confirmPassword) {
        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}


