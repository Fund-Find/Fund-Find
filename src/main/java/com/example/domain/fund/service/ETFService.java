package com.example.domain.fund.service;

import com.example.domain.fund.accessToken.AccessTokenManager;
import com.example.domain.fund.config.OpenApiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class ETFService {
    private final OpenApiConfig openApiConfig;
    private final RestTemplate restTemplate;
    private final AccessTokenManager accessTokenManager;
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public String getETFInfo(String stockCode) {
        StringBuilder result = new StringBuilder(); // 결과 데이터를 누적하여 저장하기 위한 StringBuilder 객체 생성
        try {
            String token = accessTokenManager.getAccessToken(); // 인증 토큰 가져옴

            if (token == null || token.isEmpty()) {
                log.error("액세스 토큰이 비어있습니다."); // 토큰이 없는 경우 에러 기록
                return "인증 토큰 오류가 발생했습니다.";
            }

            log.info("Access Token 확인: [{}]", token);

            String basicInfo = getBasicInfo(stockCode, token); // ETF 기본 정보를 가져옴
            String componentInfo = getComponentInfo(stockCode, token); // ETF 구성종목 정보를 가져옴

            // null이 아닌 경우 result에 추가
            if (basicInfo != null) result.append(basicInfo).append("\n\n");
            if (componentInfo != null) result.append(componentInfo).append("\n\n");

            return result.length() > 0 ? result.toString() : "ETF 정보를 불러올 수 없습니다.";

        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            return "ETF 정보를 불러올 수 없습니다: " + e.getMessage();
        }
    }

    private String getBasicInfo(String stockCode, String token) {
        try {
            log.info("========== 기본 정보 조회 시작 ==========");
            String url = openApiConfig.getBaseUrl() + "/uapi/domestic-stock/v1/quotations/inquire-price";

            // makeApiCall을 호출하여 API 요청을 보냄(API 응답을 JSON 객체(JsonNode)로 반환)
            JsonNode response = makeApiCall(url, stockCode, "FHKST01010100", token);

            // 응답이 null이 아니거나 output 필드가 있는 경우
            if (response != null && response.has("output")) {
                JsonNode output = response.get("output");
                // output: JSON에서 필요한 데이터를 추출해 가독성 좋은 문자열로 포맷
                return String.format("""
                    ETF 기본 정보:
                    종목명: %s
                    현재가: %s원
                    전일대비: %s원
                    등락률: %s%%""",
                        getNodeTextSafely(output, "hts_kor_isnm", "N/A"), // hts_kor_isnm : ETF의 한글 종목명
                        getNodeTextSafely(output, "stck_prpr", "0"), // stck_prpr : 현재가
                        getNodeTextSafely(output, "prdy_vrss", "0"), // prdy_vrss : 전일대비 가격 변화(원)
                        getNodeTextSafely(output, "prdy_ctrt", "0")); // prdy_ctrt : 전일대비 등락률(%)
            }
            // 응답이 null이거나 output 필드가 없는 경우 메시지 반환
            return "기본 정보를 불러올 수 없습니다.";
        } catch (Exception e) {
            return "기본 정보 조회 중 오류가 발생했습니다.";
        }
    }

    public String getComponentInfo(String stockCode, String token) {
        try {
            log.info("========== ETF 구성종목 정보 조회 시작 ==========");
            String url = openApiConfig.getBaseUrl() + "/uapi/etfetn/v1/quotations/inquire-component-stock-price";
            HttpHeaders headers = new HttpHeaders();
            // .set(String headerName, String headerValue) -> 주어진 headerName과 headerValue를 설정하거나,
            // 해당 키가 이미 존재할 경우 값을 덮어씀
            headers.set("authorization", "Bearer " + token);
            headers.set("appkey", openApiConfig.getApiKey());
            headers.set("appsecret", openApiConfig.getSecretKey());
            headers.set("tr_id", "FHKST121600C0");
            headers.set("custtype", "P"); // custtype: 고객 유형, P: 개인 고객
            headers.setContentType(MediaType.APPLICATION_JSON); // JSON 형식으로 요청을 전송

            // Query Parameter 설정
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                    .queryParam("FID_INPUT_ISCD", stockCode)
                    .queryParam("FID_COND_SCR_DIV_CODE", "11216"); // 화면 구분 코드

            String requestUrl = builder.toUriString();
            log.info("요청 URL: {}", requestUrl);
            log.info("요청 헤더: {}", headers);

            // restTemplate을 사용하여 get요청 수행
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // HTTP 상태 코드가 200 OK 이며, 응답 본문이 존재할 때 처리 로직 시작
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // ObjectMapper를 사용해 JSON 응답 문자열을 JsonNode로 변환하여 데이터를 파싱
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                log.info("전체 응답: {}", jsonResponse.toPrettyString());

                String rtCd = jsonResponse.path("rt_cd").asText();
                String msgCd = jsonResponse.path("msg_cd").asText();
                String msg1 = jsonResponse.path("msg1").asText();
                log.info("응답 코드: {}, 메시지 코드: {}, 메시지: {}", rtCd, msgCd, msg1);

                // jsonResponse.has를 통해 존재여부 확인하여, 해당 섹션 데이터를 로그로 출력
                // output1 : 추가 정보가 포함될 수 있는 응답 섹션
                if (jsonResponse.has("output1")) {
                    log.info("output1: {}", jsonResponse.get("output1").toPrettyString());
                } else {
                    log.warn("output1 데이터가 없습니다.");
                }

                // output2 : 구성 종목 데이터 배열
                if (jsonResponse.has("output2")) {
                    log.info("output2: {}", jsonResponse.get("output2").toPrettyString());
                } else {
                    log.warn("output2 데이터가 없습니다.");
                }

                StringBuilder result = new StringBuilder("ETF 구성종목 정보:\n");

                // output2에 구성 종목 데이터가 배열로 존재하는지 확인 후 처리
                if (jsonResponse.has("output2") && jsonResponse.get("output2").isArray()) {
                    JsonNode output2 = jsonResponse.get("output2");
                    if (output2.size() > 0) {
                        result.append(String.format("%-20s %-20s %-20s %-20s %-20s\n", "종목명", "종목코드", "구성종목 시가총액", "비중에 따른 시가총액", "비중(%)"));

                        // "=" 이걸 120번 반복 출력
                        // 결과 : "============================================================"
                        result.append("=".repeat(120)).append("\n");

                        for (JsonNode item : output2) {
                            String stockName = item.path("hts_kor_isnm").asText("N/A"); // 종목명
                            String stockCodeFromETF = item.path("stck_shrn_iscd").asText("N/A"); // 종목코드
                            String marketCapHts = item.path("hts_avls").asText("N/A"); // hts 시가총액
                            String marketCap = item.path("etf_cnfg_issu_avls").asText("0"); // 비중 시가총액
                            String weight = item.path("etf_cnfg_issu_rlim").asText("0"); // 비중
                            // - : 왼쪽정렬,
                            // 20 : 출력할 문자열 최소 폭 지정
                            // 20보다 짧으면 나머지는 공백으로 채워짐
                            // 20보다 길면 잘리지 않고 그대로 출력
                            result.append(String.format("%-20s %-20s %20s %20s %20s%%\n",
                                    stockName, stockCodeFromETF, formatNumber(marketCapHts), formatNumber(marketCap), formatWeight(weight)));
                        }
                    } else {
                        result.append("구성종목 정보가 없습니다.\n");
                    }
                } else {
                    result.append("구성종목 정보를 불러올 수 없습니다.\n");
                }
                return result.toString(); // 성공시 포맷팅 된 ETF 구성 종목 정보를 문자열로 반환
            }
            log.error("API 호출 실패 - 상태 코드: {}, 응답 본문: {}", response.getStatusCode(), response.getBody());
            return "구성종목 정보를 불러올 수 없습니다.";
        } catch (Exception e) {
            log.error("ETF 구성종목 정보 조회 중 오류 발생", e);
            return "오류가 발생했습니다: " + e.getMessage();
        }
    }

    // Api 호출을 수행하고 결과를 JsonNode로 반환하는 메서드
    private JsonNode makeApiCall(String url, String stockCode, String trId, String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.error("토큰이 비어있습니다.");
                return null;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", "Bearer " + token);
            headers.set("appkey", openApiConfig.getApiKey());
            headers.set("appsecret", openApiConfig.getSecretKey());
            headers.set("tr_id", trId);
            headers.set("custtype", "P");
            headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type : 요청 본문의 데이터 형식(여기서는 JSON)

            StringBuilder queryString = new StringBuilder();
            queryString.append("?FID_COND_MRKT_DIV_CODE=J");
            queryString.append("&FID_INPUT_ISCD=").append(stockCode);
            String requestUrl = url + queryString.toString();

            log.info("========== API 요청 정보 ==========");
            log.info("URL: {}", requestUrl);
            log.info("Headers: {}", headers);

            // API 요청 실행
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl, // 요청 URL
                    HttpMethod.GET,
                    entity, // 요청 엔터티(헤더 포함)
                    String.class // 응답을 문자열로 받음
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 응답 문자열을 JSON 형태(JsonNode)로 변환
                return objectMapper.readTree(response.getBody());
            }
            log.error("API 호출 실패. Status: {}", response.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생", e);
            return null;
        }
    }

    private String formatPrice(String price) {
        try {
            long value = Long.parseLong(price);
            return String.format("%,d", value); // 입력받은 문자열 price를 숫자로 변환한 뒤 3자리마다 쉽표(,)를 추가해 포맷팅
        } catch (NumberFormatException e) { // 입력이 숫자가 아니거나 변환할 수 없는 경우 에러 처리
            return price;
        }
    }

    private String formatWeight(String weight) {
        try {
            return String.format("%.2f", Double.parseDouble(weight)); // 소수점 둘째자리까지 표시
        } catch (NumberFormatException e) {
            return weight;
        }
    }

    private String formatNumber(String number) {
        try {
            return String.format("%,d", Long.parseLong(number));
        } catch (NumberFormatException e) {
            return number;
        }
    }

    // JSON 데이터를 표현하는 JsonNode 객체에서 특정 필드(fieldName)의 값을 안전하게 가져오고, 존재하지 않을 경우 defaultValue 반환
    private String getNodeTextSafely(JsonNode node, String fieldName, String defaultValue) {
        if (node == null) {
            return defaultValue;
        }

        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return defaultValue;
        }

        return fieldNode.asText();
    }
}