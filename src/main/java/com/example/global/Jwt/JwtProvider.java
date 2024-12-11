package com.example.global.Jwt;

import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.repository.UserRepository;
import com.example.global.Util.Util;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${custom.jwt.secretKey}")
    private String secretKeyOrigin;

    private final UserRepository userRepository;

    private SecretKey cachedSecretKey;

    // 초기화 메서드
    @PostConstruct
    private void initSecretKey() {
        if (cachedSecretKey == null) {
            cachedSecretKey = _getSecretKey();
        }
    }

    // 시크릿키 가져오기
    public SecretKey getSecretKey() {
        return cachedSecretKey;
    }

    // 시크릿키 생성
    private SecretKey _getSecretKey() {
        if (secretKeyOrigin == null || secretKeyOrigin.isEmpty()) {
            throw new IllegalArgumentException("JWT Secret Key가 설정되지 않았습니다.");
        }
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyOrigin.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    // RefreshToken 생성
    public String genRefreshToken(SiteUser user) {
        return genToken(user, 60 * 60 * 24 * 365); // 1년
    }

    // AccessToken 생성
    public String genAccessToken(SiteUser user) {
        return genToken(user, 60 * 10); // 10분
    }

    // 토큰 생성
    public String genToken(SiteUser user, int seconds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        long now = new Date().getTime();
        Date expiration = new Date(now + 1000L * seconds);

        return Jwts.builder()
                .claim("body", Util.json.toStr(claims))
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰 검증
    public boolean verify(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getClaims(String token) {
        try {
            System.out.println("토큰 검증 시작: " + token);

            String body = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("body", String.class);

            System.out.println("JWT Claims: " + body);

            return Util.toMap(body);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // 리프레시 토큰으로 액세스 토큰 갱신
    public String refreshAccessToken(String refreshToken) {
        if (verify(refreshToken)) {
            SiteUser user = getUserFromRefreshToken(refreshToken);
            return genAccessToken(user);
        }
        throw new IllegalArgumentException("리프레시 토큰이 만료되었거나 잘못되었습니다.");
    }

    // 리프레시 토큰에서 사용자 정보 추출
    public SiteUser getUserFromRefreshToken(String refreshToken) {
        Map<String, Object> claims = getClaims(refreshToken);
        if (claims != null) {
            String username = (String) claims.get("username");
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        }
        throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
    }
}
