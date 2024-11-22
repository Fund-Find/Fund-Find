package com.example.domain.fund.service;

import com.example.domain.fund.accessToken.AccessTokenManager;
import com.example.domain.fund.config.OpenApiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j // 로깅을 위한 Lombok 어노테이션으로, log 객체를 통해 로깅 기능을 사용가능
public class ETFService {
    private final OpenApiConfig openApiConfig;
    private final RestTemplate restTemplate;
    private final AccessTokenManager accessTokenManager;  // 추가

    public String getStockInfo(String stockCode) {
        String url = openApiConfig.getBaseUrl() + "/uapi/domestic-stock/v1/quotations/inquire-price";

        try {
            // 필수 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessTokenManager.getAccessToken());  // 액세스 토큰 사용
            headers.add("appkey", openApiConfig.getApiKey());
            headers.add("appsecret", openApiConfig.getSecretKey());
            headers.add("tr_id", "FHKST01010100");  // 필수 tr_id 추가
            headers.add("custtype", "P");  // 개인투자자 타입
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 파라미터 설정
            // FID_COND_MRKT_DIV_CODE=J: 시장 구분 코드 (J는 국내시장)
            // FID_INPUT_ISCD=%s: 주식 종목 코드 (ex: 069500)
            String queryString = String.format("?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=%s", stockCode);

            // 디버깅을 위한 로그 추가
            log.info("Request URL: {}", url + queryString);
            log.info("Request Headers: {}", headers);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // API 호출
            // RestTemplate.exchange:
            // -> HTTP GET 요청 보냄
            // -> 요청 헤더는 HttpEntity 객체에 포함
            // -> API 응답은 ResponseEntity<String> 형태로 반환
            ResponseEntity<String> response = restTemplate.exchange(
                    url + queryString,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // API 응답 로그 추가
            log.debug("API Response: {}", response.getBody());

            // 응답 처리
            ObjectMapper mapper = new ObjectMapper();
            // JSON 문자열을 파싱하여 트리구조의 객체로 변환하는 작업 수행
            JsonNode root = mapper.readTree(response.getBody());

            // output 노드 가져오기 및 null 체크
            // 응답 객체에서 output 필드 추출 (API 문서에서 지정된 데이터 구조)
            JsonNode output = root.get("output");

            // 응답 데이터에 output 필드가 없을 경우 처리
            if (output == null) {
                log.error("API 응답에 output 필드가 없습니다.");
                return "데이터를 불러올 수 없습니다.";
            }

            // 각 필드에 대해 안전하게 값 추출
            // getNodeTextSafely: NullPointerException 방지 메서드로, JSON 노드에서 안전하게 값을 가져옴.
            // 값이 없을 경우 기본값 반환.
            String name = getNodeTextSafely(output, "hts_kor_isnm", "N/A");
            String price = getNodeTextSafely(output, "stck_prpr", "0");
            String priceChange = getNodeTextSafely(output, "prdy_vrss", "0");
            String changeRate = getNodeTextSafely(output, "prdy_ctrt", "0");

            return String.format("종목명: %s\n현재가: %s원\n전일대비: %s원\n등락률: %s%%",
                    name, price, priceChange, changeRate);

            // 예외 발생 시 오류를 로그로 기록하고 사용자에게 간단한 메시지 반환
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생", e);
            return "Error: " + e.getMessage();
        }
    }

    // JSON 노드에서 특정 필드를 안전하게 가져오는 유틸리티 메서드
    // 필드가 없거나 null이면 기본값을 반환
    private String getNodeTextSafely(JsonNode node, String fieldName, String defaultValue) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : defaultValue;
    }
}