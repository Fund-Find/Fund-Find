package com.example.domain.fund.service;

import com.example.domain.fund.entity.ETF;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import com.example.domain.fund.repository.ETFRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.domain.fund.model.ETFCategory.*;
import static com.example.domain.fund.model.ETFSubCategory.*;

@Service
@RequiredArgsConstructor
public class ETFInitService {

    private final ETFRepository etfRepository;

    @Transactional
    public void initializeETFData() {
        // 주식형 ETF - 대형주
        saveETF("069500", "KODEX 200", STOCK, LARGE_CAP);
        saveETF("102110", "TIGER 200", STOCK, LARGE_CAP);
        saveETF("148020", "KBSTAR 200", STOCK, LARGE_CAP);
        saveETF("226490", "KODEX KOSPI 100", STOCK, LARGE_CAP);
        saveETF("292150", "TIGER KOSPI 100", STOCK, LARGE_CAP);

        // 주식형 ETF - 중형주
        saveETF("091160", "KODEX 중소형", STOCK, SMALL_MID_CAP);
        saveETF("200250", "TIGER 중소형성장", STOCK, SMALL_MID_CAP);
        saveETF("232080", "TIGER 중소형가치", STOCK, SMALL_MID_CAP);
        saveETF("243880", "KODEX 중형성장", STOCK, SMALL_MID_CAP);
        saveETF("277630", "TIGER 중형성장", STOCK, SMALL_MID_CAP);

        // 채권형 ETF - 국내채권
        saveETF("153130", "KODEX 단기채권", BOND, DOMESTIC_BOND);
        saveETF("152380", "TIGER 단기채권", BOND, DOMESTIC_BOND);
        saveETF("136340", "KBSTAR 중기채권", BOND, DOMESTIC_BOND);
        saveETF("148070", "KODEX 국고채", BOND, DOMESTIC_BOND);
        saveETF("157450", "TIGER 국고채", BOND, DOMESTIC_BOND);

        // 채권형 ETF - 해외채권
        saveETF("280930", "KODEX 미국채10년", BOND, FOREIGN_BOND);
        saveETF("305080", "TIGER 미국채10년", BOND, FOREIGN_BOND);
        saveETF("305720", "KBSTAR 미국채10년", BOND, FOREIGN_BOND);
        saveETF("310970", "TIGER 차이나채권", BOND, FOREIGN_BOND);
        saveETF("319640", "KODEX 차이나채권", BOND, FOREIGN_BOND);

        // 원자재 ETF - 귀금속
        saveETF("132030", "KODEX 골드선물", COMMODITY, PRECIOUS_METAL);
        saveETF("139220", "TIGER 골드선물", COMMODITY, PRECIOUS_METAL);
        saveETF("091170", "KBSTAR 금현물", COMMODITY, PRECIOUS_METAL);
        saveETF("130680", "TIGER 금은혼합", COMMODITY, PRECIOUS_METAL);
        saveETF("091160", "KODEX 은선물", COMMODITY, PRECIOUS_METAL);

        // 원자재 ETF - 에너지
        saveETF("261220", "KODEX WTI원유선물", COMMODITY, ENERGY);
        saveETF("217770", "TIGER WTI원유선물", COMMODITY, ENERGY);
        saveETF("130730", "KODEX 천연가스선물", COMMODITY, ENERGY);
        saveETF("139230", "TIGER 천연가스선물", COMMODITY, ENERGY);
        saveETF("261240", "KBSTAR WTI원유선물", COMMODITY, ENERGY);

        // 섹터별 ETF - IT/반도체
        saveETF("091160", "KODEX 반도체", SECTOR, IT_SEMICONDUCTOR);
        saveETF("091180", "TIGER 반도체", SECTOR, IT_SEMICONDUCTOR);
        saveETF("365040", "TIGER IT", SECTOR, IT_SEMICONDUCTOR);
        saveETF("091990", "KBSTAR 반도체", SECTOR, IT_SEMICONDUCTOR);
        saveETF("091170", "TIGER IT/반도체", SECTOR, IT_SEMICONDUCTOR);

        // 섹터별 ETF - 금융
        saveETF("091170", "KODEX 은행", SECTOR, FINANCE);
        saveETF("091180", "TIGER 은행", SECTOR, FINANCE);
        saveETF("139250", "KODEX 금융", SECTOR, FINANCE);
        saveETF("102960", "TIGER 금융", SECTOR, FINANCE);
        saveETF("117700", "KBSTAR 금융", SECTOR, FINANCE);

        // 섹터별 ETF - 헬스케어
        saveETF("244580", "KODEX 바이오", SECTOR, HEALTHCARE);
        saveETF("245710", "TIGER 바이오", SECTOR, HEALTHCARE);
        saveETF("214980", "KBSTAR 헬스케어", SECTOR, HEALTHCARE);
        saveETF("143860", "TIGER 헬스케어", SECTOR, HEALTHCARE);
        saveETF("143850", "KODEX 헬스케어", SECTOR, HEALTHCARE);

        // 고위험 ETF - 레버리지
        saveETF("252670", "KODEX 200선물레버리지", HighLisk, LEVERAGE);
        saveETF("233740", "TIGER 코스닥150레버리지", HighLisk, LEVERAGE);
        saveETF("462330", "KODEX 2차전지산업레버리지", HighLisk, LEVERAGE);

        // 고위험 ETF - 인버스
        saveETF("252710", "KODEX 200선물인버스2X", HighLisk, INVERSE);
        saveETF("145670", "TIGER 200선물인버스", HighLisk, INVERSE);
        saveETF("123310", "KODEX 코스닥150선물인버스", HighLisk, INVERSE);
    }

    private void saveETF(String code, String name, ETFCategory category, ETFSubCategory subCategory) {
        ETF etf = ETF.builder()
                .code(code)
                .name(name)
                .category(category)
                .subCategory(subCategory)
                .build();
        etfRepository.save(etf);
    }
}
