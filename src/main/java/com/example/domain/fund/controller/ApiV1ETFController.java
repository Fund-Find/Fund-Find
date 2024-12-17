package com.example.domain.fund.controller;

import com.example.domain.fund.accessToken.AccessTokenManager;
import com.example.domain.fund.entity.ETF;
import com.example.domain.fund.service.ETFInitService;
import com.example.domain.fund.service.ETFService;
import com.example.domain.propensity.dto.PropensityDTO;
import com.example.domain.propensity.service.PropensityService;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import com.example.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/etf")
@CrossOrigin(origins = "http://localhost:5173")
public class ApiV1ETFController {

    private final ETFService etfService;
    private final PropensityService propensityService;
    private final ETFInitService etfInitService;
    private final AccessTokenManager accessTokenManager; // 토큰 상태 테스트 용
    private final UserService userService;



    @GetMapping("/{code}")
    public RsData<String> getETFInfo(@PathVariable("code") String code) {
        try {
            log.info("ETF 정보 조회 시작 - 종목코드: {}", code);

            // 토큰 상태 확인
            String token = accessTokenManager.getAccessToken();
            log.info("현재 사용 중인 토큰: {}", token);

            String etfInfo = etfService.getETFInfo(code);
            log.info("ETF 정보 조회 결과: {}", etfInfo);

            return RsData.of("200", "ETF 정보 조회 성공", etfInfo);
        } catch (Exception e) {
            return RsData.of("500", "ETF 정보 조회 실패: " + e.getMessage());
        }
    }

    @PostMapping("/survey/submit")
    public ResponseEntity<RsData<Long>> submitSurvey(@RequestBody Map<String, String> answers,@CookieValue(value = "accessToken", required = false) String accessToken) {
        try {
            if (accessToken == null) {
                System.out.println("Access Token이 쿠키에서 발견되지 않았습니다.");
                return ResponseEntity.badRequest().body(RsData.of("403", "엑세스 토큰이 없습니다.", null));
            }

            String mbti = propensityService.calculateMBTI(answers);

            SiteUser user = this.userService.getSiteUserFromAccessToken(accessToken);


            PropensityDTO savedPropensity = propensityService.processAndSavePropensity(answers, user);

            return ResponseEntity.ok(RsData.of("200", "투자 성향 MBTI 등록 성공", savedPropensity.getPropensityId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RsData.of("400", "투자 성향 MBTI 등록 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/propensity/{id}")
    public ResponseEntity<RsData<PropensityDTO>> getPropensity(@PathVariable("id") Long id) {
        try {
            PropensityDTO propensity = propensityService.getPropensityById(id);
            return ResponseEntity.ok(RsData.of("200", "투자성향 조회 성공", propensity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RsData.of("400", e.getMessage(), null));
        }
    }

    @GetMapping("/recommend/{id}")
    public ResponseEntity<RsData<List<Map<String, String>>>> getRecommendedETFs(@PathVariable("id") Long id) {
        try {
            List<ETF> recommendedETFs = propensityService.getRecommendedETFsById(id);
            List<Map<String, String>> etfDetails = new ArrayList<>();

            for (ETF etf : recommendedETFs) {
                Map<String, String> details = new HashMap<>();
                details.put("code", etf.getCode());
                details.put("name", etf.getName());
                details.put("category", etf.getCategory().getDescription());
                details.put("subCategory", etf.getSubCategory().getDescription());

                // 실시간 데이터 조회
                try {
                    String etfInfo = etfService.getETFInfo(etf.getCode());
                    if (etfInfo != null && !etfInfo.isEmpty()) {
                        Map<String, String> apiDetails = etfService.parseETFInfo(etfInfo);
                        details.putAll(apiDetails);
                    }
                } catch (Exception e) {
                    log.error("실시간 데이터 조회 실패", e);
                }

                // 기본값 설정
                details.putIfAbsent("currentPrice", etf.getPrice());
                details.putIfAbsent("componentCount", etf.getComponentCount());
                details.putIfAbsent("netAsset", etf.getNetAsset());
                details.putIfAbsent("nav", etf.getNav());
                details.putIfAbsent("prevNav", etf.getPrevNav());
                details.putIfAbsent("navChange", etf.getNavChange());
                details.putIfAbsent("dividendCycle", etf.getDividendCycle());
                details.putIfAbsent("company", etf.getCompany());
                details.putIfAbsent("priceChange", etf.getPriceChange());
                details.putIfAbsent("priceChangeRate", etf.getPriceChangeRate());

                etfDetails.add(details);
                Thread.sleep(100);
            }

            return ResponseEntity.ok(RsData.of("200", "추천 ETF 조회 성공", etfDetails));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RsData.of("400", "추천 ETF 조회 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/list")
    public RsData<List<Map<String, String>>> getAllETFs() {
        try {
            List<ETF> etfs = etfInitService.getAllETFs();
            if (etfs == null || etfs.isEmpty()) {
                return RsData.of("404", "ETF 데이터가 존재하지 않습니다.", null);
            }

            List<Map<String, String>> etfDetails = new ArrayList<>();
            for (ETF etf : etfs) {
                Map<String, String> details = new HashMap<>();
                details.put("code", etf.getCode());
                details.put("name", etf.getName());
                details.put("category", etf.getCategory().getDescription());
                details.put("subCategory", etf.getSubCategory().getDescription());

                // 실시간 데이터 조회
                try {
                    String etfInfo = etfService.getETFInfo(etf.getCode());
                    if (etfInfo != null && !etfInfo.isEmpty()) {
                        Map<String, String> apiDetails = etfService.parseETFInfo(etfInfo);
                        details.putAll(apiDetails);
                    }
                } catch (Exception e) {
                    log.error("실시간 데이터 조회 실패", e);
                }

                // 실시간 데이터 조회 실패시 기본값 설정
                details.putIfAbsent("currentPrice", etf.getPrice());
                details.putIfAbsent("componentCount", etf.getComponentCount());
                details.putIfAbsent("netAsset", etf.getNetAsset());
                details.putIfAbsent("nav", etf.getNav());
                details.putIfAbsent("prevNav", etf.getPrevNav());
                details.putIfAbsent("navChange", etf.getNavChange());
                details.putIfAbsent("dividendCycle", etf.getDividendCycle());
                details.putIfAbsent("company", etf.getCompany());
                details.putIfAbsent("priceChange", etf.getPriceChange());
                details.putIfAbsent("priceChangeRate", etf.getPriceChangeRate());

                etfDetails.add(details);
                Thread.sleep(100);  // API 호출 제한 고려
            }

            return RsData.of("200", "ETF 목록 조회 성공", etfDetails);
        } catch (Exception e) {
            log.error("ETF 목록 조회 중 오류 발생", e);
            return RsData.of("500", "ETF 목록 조회 실패: " + e.getMessage(), null);
        }
    }



}