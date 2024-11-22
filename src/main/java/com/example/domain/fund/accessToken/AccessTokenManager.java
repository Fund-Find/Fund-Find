package com.example.domain.fund.accessToken;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AccessTokenManager {
    private final String TOKEN_URL = "https://openapi.koreainvestment.com:9443/oauth2/tokenP";
    private final String APP_KEY = "PSlBkPCX0esNWc5Tv1vDYh37FZnf6BPlEACX";
    private final String APP_SECRET = "ucJXLQefoLvC3ObryidOsMBx/DF50MpaKYArOCcyfGStu0og5psl4oR60B2sI55RM9slwmPN/tDvKGS0k+R+1eGR4N4lWltyMtG9DnV1/GLB8h69vcMpm3uHd8EoY5wHGMidM11wblylmKAv3hc+jcyHFe+yyF4BBAhqxwHMaxNoNLpFEd0=";

    private String accessToken;
    private long expiryTime;
    private long lastTokenRefreshTime;
    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000; // 토큰의 유효 기간을 하루(24시간)로 설정

    // Spring에서 제공하는 REST API 호출용 객체
    private final RestTemplate restTemplate;

    public AccessTokenManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 여러 스레드에서 동시에 토큰을 갱신하지 않도록 동기화 처리
    private synchronized void refreshTokenIfNeeded() {

        long currentTime = System.currentTimeMillis();

        // 토큰이 없거나 만료되었을 때만 갱신
        if (accessToken == null || currentTime >= expiryTime) {
            try {
                // 마지막 갱신으로부터 1일이 지났는지 확인
                if (lastTokenRefreshTime > 0 && (currentTime - lastTokenRefreshTime) < 86400000) { // -> 60000에서 1일로 변경
                    log.debug("Waiting for token refresh cooldown...");
                    return;
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> requestBody = new HashMap<>();
                // client_credentials -> OAuth 2.0에서 사용되는 grant_type 중 하나,
                // 클라이언트 애플리케이션이 특정 자원(API)에 접근하기 위해 서버로부터 액세스 토큰을 요청할 때 사용하는 방식
                requestBody.put("grant_type", "client_credentials");
                requestBody.put("appkey", APP_KEY);
                requestBody.put("appsecret", APP_SECRET);

                HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

                log.debug("Requesting new access token...");
                // restTemplate.postForEntity로 POST 요청을 전송
                // 응답은 JSON 형식으로 반환
                ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, entity, String.class);

                // ObjectMapper를 사용해 JSON 응답 데이터를 파싱
                // access_token 필드가 있으면 새 액세스 토큰을 저장하고 만료 시간을 설정
                if (response.getStatusCode().is2xxSuccessful()) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());

                    if (root.has("access_token")) {
                        accessToken = root.get("access_token").asText();
                        lastTokenRefreshTime = currentTime;
                        expiryTime = currentTime + ONE_DAY_IN_MILLIS;
                        log.info("Successfully obtained new access token");
                    }
                }
            } catch (Exception e) {
                log.error("Failed to refresh token", e);
                // 에러가 발생해도 앱이 중단되지 않도록 함
            }
        }
    }

    // 필요할 때마다 refreshTokenIfNeeded를 호출해 토큰을 갱신
    public String getAccessToken() {
        refreshTokenIfNeeded();
        return accessToken != null ? accessToken : "";
    }

    // 현재 토큰이 유효한지 확인
    public boolean isTokenValid() {
        return accessToken != null && System.currentTimeMillis() < expiryTime;
    }
}