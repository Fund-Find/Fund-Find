package com.example.domain.user.service;

import com.example.domain.user.dto.request.UserPatchRequest;
import com.example.domain.user.dto.request.UserRequest;
import com.example.domain.user.dto.response.UserResponse;
import com.example.domain.user.entity.SiteUser;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            System.out.println("이미지 :http://localhost:8080/uploads/"+newImageUrl);
            existingUser.setThumbnailImg("http://localhost:8080/uploads/"+newImageUrl.substring(newImageUrl.lastIndexOf("\\") + 1)); //
        }

        SiteUser updatedUser = userRepository.save(existingUser);
        return UserResponse.fromEntity(updatedUser);
    }


    public SiteUser getUser(String username) {
        return this.userRepository.findByUsername(username)
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
}


