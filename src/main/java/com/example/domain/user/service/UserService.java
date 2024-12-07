    package com.example.domain.user.service;

    import com.example.domain.user.dto.request.UserRequest;
    import com.example.domain.user.dto.response.UserResponse;
    import com.example.domain.user.entity.SiteUser;
    import com.example.domain.user.repository.UserRepository;
    import com.example.global.Jwt.JwtProvider;
    import com.example.global.RsData.RsData;
    import com.example.global.security.SecurityUser;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    @Transactional
    public class UserService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final FileStorageService fileStorageService;
        private JwtProvider jwtProvider;


        // 회원가입
        public SiteUser registerUser(UserRequest request) {
            // 비밀번호와 비밀번호 확인란 비교
            if (!request.getPassword1().equals(request.getPassword2())) {
                throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
            }

            // 중복 확인
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }

            // 프로필 이미지 처리
            String profileImageUrl = "/img/login-icon.svg"; // 기본 프로필 이미지 경로
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
            SiteUser user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException("존재하지 않는 리프레시 토큰입니다."));

            String accessToken = jwtProvider.genAccessToken(user);

            return RsData.of("200", "토큰 갱신 성공", accessToken);
        }
        public UserResponse updateUser(SiteUser existingUser, UserRequest updatedData) {
            if (updatedData.getPassword1() != null) {
                existingUser.setPassword(passwordEncoder.encode(updatedData.getPassword1()));
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
            // 이미지 변경 처리
            if (updatedData.getThumbnailImg() != null && !updatedData.getThumbnailImg().isEmpty()) {
                if (!existingUser.getThumbnailImg().equals("/img/login-icon.svg")) {
                    fileStorageService.deleteFile(existingUser.getThumbnailImg());
                }
                String newImageUrl = fileStorageService.storeFile(updatedData.getThumbnailImg());
                existingUser.setThumbnailImg(newImageUrl);
            }

            SiteUser updatedUser = userRepository.save(existingUser);
            return UserResponse.fromEntity(updatedUser);
        }


        public SiteUser getUser(String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        }


    //    public String authenticateAndGenerateToken(UserRequest request) {
    //        // 사용자 확인
    //        SiteUser user = userRepository.findByUsername(request.getUsername())
    //                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    //
    //        // 비밀번호 검증
    //        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    //            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    //        }
    //
    //        // JWT 생성
    //        return jwtService.generateToken(user);
    //    }
    //
    //    public UserResponse getUserFromToken(String token) {
    //        if (!jwtService.validateToken(token)) {
    //            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
    //        }
    //
    //        String username = jwtService.extractUsername(token);
    //        SiteUser user = userRepository.findByUsername(username)
    //                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    //
    //        return UserResponse.fromEntity(user);


        // 사용자 정보 조회
        public Optional<SiteUser> findUserByUsername(String username) {
            return userRepository.findByUsername(username);
        }


        // 사용자 삭제
        public void deleteUser(String username) {
            SiteUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            userRepository.delete(user);
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
    }


