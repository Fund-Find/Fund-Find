    package com.example.domain.user.service;

    import com.example.domain.user.dto.request.UserRequest;
    import com.example.domain.user.dto.response.UserResponse;
    import com.example.domain.user.entity.SiteUser;
    import com.example.domain.user.repository.UserRepository;
    import com.example.global.Jwt.JwtProvider;
    import com.example.global.rsData.RsData;
    import com.example.global.security.SecurityUser;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
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
        public SiteUser getSiteUserFromAccessToken(String accessToken) {
            // JWT 토큰에서 클레임 추출
            Map<String, Object> payloadBody = jwtProvider.getClaims(accessToken);

            // SiteUser 객체 생성
            SiteUser user = new SiteUser();

            // 클레임에서 프로필 정보 추출하여 setter로 값 설정
            user.setUsername((String) payloadBody.get("username"));
            user.setNickname((String) payloadBody.get("nickname"));
            user.setThumbnailImg((String) payloadBody.get("ThumbnailImg"));
            user.setEmail((String) payloadBody.get("email"));
            user.setIntro((String) payloadBody.get("intro"));

            // 필요한 정보를 설정한 SiteUser 객체 반환
            return user;
        }
    }


