package com.example.domain.fund.controller;

import com.example.domain.fund.entity.ETF;
import com.example.domain.fund.service.ETFService;
import com.example.domain.propersity.dto.PropensityDTO;
import com.example.domain.propersity.service.PropensityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/etf")
public class ApiV1ETFController {

    private final ETFService etfService;
    private final PropensityService propensityService;

    @GetMapping("/{code}")
    public com.example.global.RsData.RsData<String> getETFInfo(@PathVariable("code") String code) {
        try {
            String etfInfo = etfService.getETFInfo(code);
            return com.example.global.RsData.RsData.of("200", "ETF 정보 조회 성공", etfInfo);
        } catch (Exception e) {
            return com.example.global.RsData.RsData.of("500", "ETF 정보 조회 실패: " + e.getMessage());
        }
    }

    @PostMapping("/survey/submit")
    public ResponseEntity<com.example.global.RsData.RsData<Long>> submitSurvey(@RequestBody Map<String, String> answers) {
        try {

            String mbti = propensityService.calculateMBTI(answers);

            PropensityDTO savedPropensity = propensityService.processAndSavePropensity(answers);

            return ResponseEntity.ok(com.example.global.RsData.RsData.of("200", "투자 성향 MBTI 등록 성공", savedPropensity.getPropensityId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(com.example.global.RsData.RsData.of("400", "투자 성향 MBTI 등록 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/propensity/{id}")
    public ResponseEntity<com.example.global.RsData.RsData<PropensityDTO>> getPropensity(@PathVariable("id") Long id) {
        try {
            PropensityDTO propensity = propensityService.getPropensityById(id);
            return ResponseEntity.ok(com.example.global.RsData.RsData.of("200", "투자성향 조회 성공", propensity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(com.example.global.RsData.RsData.of("400", e.getMessage(), null));
        }
    }

    @GetMapping("/recommend/{id}")
    public ResponseEntity<com.example.global.RsData.RsData<List<ETF>>> getRecommendedETFs(@PathVariable("id") Long id) {
        try {
            List<ETF> recommendedETFs = propensityService.getRecommendedETFsById(id);
            return ResponseEntity.ok(com.example.global.RsData.RsData.of("200", "추천 ETF 조회 성공", recommendedETFs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(com.example.global.RsData.RsData.of("400", "추천 ETF 조회 실패: " + e.getMessage(), null));
        }
    }

}