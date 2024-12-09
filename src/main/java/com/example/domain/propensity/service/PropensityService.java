package com.example.domain.propersity.service;

import com.example.domain.fund.entity.ETF;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import com.example.domain.fund.repository.ETFRepository;
import com.example.domain.propersity.dto.PropensityDTO;
import com.example.domain.propersity.entity.Propensity;
import com.example.domain.propersity.repository.PropensityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PropensityService {

    private final ETFRepository etfRepository;
    private final PropensityRepository propensityRepository;

    // 설문 응답 기반 투자 성향 MBTI 계산
    public String calculateMBTI(Map<String, String> answers) {
        StringBuilder mbti = new StringBuilder();

        mbti.append(answers.getOrDefault("q1", "option1").equals("option1") ? "I" : "E");
        mbti.append(answers.getOrDefault("q2", "option1").equals("option1") ? "S" : "N");
        mbti.append(answers.getOrDefault("q3", "option1").equals("option1") ? "T" : "F");
        mbti.append(answers.getOrDefault("q4", "option1").equals("option1") ? "J" : "P");

        return mbti.toString();
    }

    public PropensityDTO processAndSavePropensity(Map<String, String> answers) {
        String mbtiResult = calculateMBTI(answers);

        Propensity propensity = new Propensity();
        propensity.setSurveyAnswer(answers.toString());
        propensity.setSurveyResult(mbtiResult);
        Propensity savedPropensity = propensityRepository.save(propensity);

        return PropensityDTO.builder()
                .propensityId(savedPropensity.getPropensityId())
                .surveyAnswer(savedPropensity.getSurveyAnswer())
                .surveyResult(savedPropensity.getSurveyResult())
                .build();
    }

    public PropensityDTO getPropensityById(Long id) {
        Propensity propensity = propensityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 투자성향 데이터가 존재하지 않습니다: " + id));

        return PropensityDTO.builder()
                .propensityId(propensity.getPropensityId())
                .surveyAnswer(propensity.getSurveyAnswer())
                .surveyResult(propensity.getSurveyResult())  // MBTI 결과
                .build();
    }

    public List<ETF> getRecommendedETFsById(Long id) {
        // PropensityRepository를 통해 데이터를 가져오고 비즈니스 로직 수행
        Propensity propensity = propensityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 투자 성향 데이터가 존재하지 않습니다."));

        // MBTI를 가져온 후 추천 ETF를 조회
        String mbti = propensity.getSurveyResult();
        return getRecommendedETFs(mbti);
    }

    // 투자 성향 MBTI 기반 추천 ETF 조회
    public List<ETF> getRecommendedETFs(String mbti) {
        switch (mbti) {
            // --- 내향적(I) + 판단형(J) 성향: 보수적이고 안정적인 투자 선호 ---
            case "ISTJ":  // 현실적, 신중한 성향
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.DOMESTIC_BOND);
            case "ISFJ":  // 안정 지향적, 보수적 성향
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.STOCK, ETFSubCategory.LARGE_CAP);
            case "INFJ":  // 가치 지향적, 장기 투자 선호
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.SECTOR, ETFSubCategory.HEALTHCARE);
            case "INTJ":  // 전략적, 분석적 성향
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.SECTOR, ETFSubCategory.IT_SEMICONDUCTOR);

            // --- 내향적(I) + 인식형(P) 성향: 신중하지만 기회 포착 중시 ---
            case "ISTP":  // 논리적, 실용적 성향
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.SECTOR, ETFSubCategory.IT_SEMICONDUCTOR);
            case "ISFP":  // 예술적, 가치 중시 성향
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.COMMODITY, ETFSubCategory.PRECIOUS_METAL);
            case "INFP":  // 이상주의적, 혁신 중시
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.SECTOR, ETFSubCategory.HEALTHCARE);
            case "INTP":  // 분석적, 혁신 지향적
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.STOCK, ETFSubCategory.SMALL_MID_CAP);

            // --- 외향적(E) + 인식형(P) 성향: 적극적이고 도전적인 투자 성향 ---
            case "ESTP":  // 모험적, 기회주의적 성향
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.HighLisk, ETFSubCategory.LEVERAGE);
            case "ESFP":  // 즉흥적, 기회 포착 성향
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.STOCK, ETFSubCategory.SMALL_MID_CAP);
            case "ENFP":  // 열정적, 새로운 기회 추구
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.SECTOR, ETFSubCategory.IT_SEMICONDUCTOR);
            case "ENTP":  // 혁신적, 도전적 성향
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.HighLisk, ETFSubCategory.LEVERAGE);

            // --- 외향적(E) + 판단형(J) 성향: 계획적이고 목표 지향적인 투자 ---
            case "ESTJ":  // 체계적, 효율성 중시
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.SECTOR, ETFSubCategory.FINANCE);
            case "ESFJ":  // 조화로운, 안정 추구
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.FOREIGN_BOND);
            case "ENFJ":  // 성장 지향적, 영향력 추구
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.STOCK, ETFSubCategory.LARGE_CAP);
            case "ENTJ":  // 전략적, 성취 지향적
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.SECTOR, ETFSubCategory.IT_SEMICONDUCTOR);

            default:
                // 성향을 판단할 수 없는 경우 안정적인 대형주 ETF 추천
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.STOCK, ETFSubCategory.LARGE_CAP);
        }
    }
}

