package com.example.domain.fund.service;

import com.example.domain.fund.entity.ETF;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import com.example.domain.fund.repository.ETFRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.domain.fund.model.ETFCategory.*;
import static com.example.domain.fund.model.ETFSubCategory.*;

@Service
@RequiredArgsConstructor
public class ETFInitService {

    private final ETFRepository etfRepository;

    @Transactional
    public void initializeETFData() {

        // ---------------------------------------- 주식형 ETF --------------------------------------

        // 주식형 ETF - 대형주 (8개)
        saveETF("069500", "KODEX 200", STOCK, LARGE_CAP);
        saveETF("102110", "TIGER 200", STOCK, LARGE_CAP);
        saveETF("148020", "RISE 200(KBSTAR 200)", STOCK, LARGE_CAP);
        saveETF("226490", "KODEX KOSPI", STOCK, LARGE_CAP);
        saveETF("359210", "KODEX KOSPI TR", STOCK, LARGE_CAP);
        saveETF("237350", "KODEX KOSPI 100", STOCK, LARGE_CAP);
        saveETF("337140", "KODEX KOSPI 대형주", STOCK, LARGE_CAP);
        saveETF("292150", "TIGER TOP10", STOCK, LARGE_CAP);

        // 주식형 ETF - 중형주 (10개)
        saveETF("091160", "KODEX 반도체", STOCK, SMALL_MID_CAP);
        saveETF("461450", "KODEX KOSDAQ 글로벌", STOCK, SMALL_MID_CAP);
        saveETF("467540", "KOSEF KOSDAQ 글로벌", STOCK, SMALL_MID_CAP);
        saveETF("461580", "TIGER KOSDAQ 글로벌", STOCK, SMALL_MID_CAP);
        saveETF("316670", "KOSEF KOSDAQ 150", STOCK, SMALL_MID_CAP);
        saveETF("270810", "RISE KOSDAQ 150", STOCK, SMALL_MID_CAP);
        saveETF("229200", "KODEX KOSDAQ 150", STOCK, SMALL_MID_CAP);
        saveETF("301400", "PLUS KOSDAQ 150", STOCK, SMALL_MID_CAP);
        saveETF("354500", "ACE KOSDAQ 150", STOCK, SMALL_MID_CAP);
        saveETF("304770", "HANARO KOSDAQ 150", STOCK, SMALL_MID_CAP);


        // ----------------------------------------- 채권형 ETF ---------------------------------------

        // 채권형 ETF - 국내채권 (9개)
        saveETF("447770", "TIGER 테슬라채권혼합Fn", BOND, DOMESTIC_BOND);
        saveETF("472170", "TIGER 미국테크TOP10채권혼합", BOND, DOMESTIC_BOND);
        saveETF("448540", "ACE 엔비디아채권혼합블룸버그", BOND, DOMESTIC_BOND);
        saveETF("435420", "TIGER 미국나스닥100TR채권혼합Fn", BOND, DOMESTIC_BOND);
        saveETF("363570", "KODEX 장기종합채권(AA-이상)액티브", BOND, DOMESTIC_BOND);
        saveETF("454780", "히어로즈 종합채권(AA-이상)액티브", BOND, DOMESTIC_BOND);
        saveETF("451540", "TIGER 종합채권(AA-이상)액티브", BOND, DOMESTIC_BOND);
        saveETF("451000", "PLUS 종합채권(AA-이상)액티브", BOND, DOMESTIC_BOND);
        saveETF("461500", "HANARO 종합채권(AA-이상)액티브", BOND, DOMESTIC_BOND);

        // 채권형 ETF - 해외채권 (8개)
        saveETF("449580", "RISE 미국빅데이터Top3채권혼합", BOND, FOREIGN_BOND);
        saveETF("438100", "ACE 미국나스닥100채권혼합액티브", BOND, FOREIGN_BOND);
        saveETF("447620", "SOL 미국TOP5채권혼합40 Solactive", BOND, FOREIGN_BOND);
        saveETF("438080", "ACE 미국S&P500채권혼합액티브", BOND, FOREIGN_BOND);
        saveETF("437070", "KODEX 아시아달러채권ESG플러스액티브", BOND, FOREIGN_BOND);
        saveETF("469580", "RISE KP달러채권액티브", BOND, FOREIGN_BOND);
        saveETF("440650", "ACE 미국달러단기채권액티브", BOND, FOREIGN_BOND);
        saveETF("329750", "TIGER 미국달러단기채권액티브", BOND, FOREIGN_BOND);


        // ----------------------------------- 원자재 ETF -------------------------------------------

        // 원자재 ETF - 귀금속 (6개)
        saveETF("319640", "TIGER 골드선물(H)", COMMODITY, PRECIOUS_METAL);
        saveETF("132030", "KODEX 골드선물(H)", COMMODITY, PRECIOUS_METAL);
        saveETF("473640", "HANARO 글로벌금채굴기업", COMMODITY, PRECIOUS_METAL);
        saveETF("139310", "TIGER 금속선물(H)", COMMODITY, PRECIOUS_METAL);
        saveETF("139320", "TIGER 금은선물(H)", COMMODITY, PRECIOUS_METAL);
        saveETF("144600", "KODEX 은선물(H)", COMMODITY, PRECIOUS_METAL);

        // 원자재 ETF - 에너지 (6개)
        saveETF("261220", "KODEX WTI원유선물", COMMODITY, ENERGY);
        saveETF("217770", "TIGER 원유선물인버스(H)", COMMODITY, ENERGY);
        saveETF("474800", "KOSEF 미국 원유에너지기업", COMMODITY, ENERGY);
        saveETF("219390", "RISE 미국S&P원유생산기업(합성 H)", COMMODITY, ENERGY);
        saveETF("130680", "TIGER 원유선물Enhanced(H)", COMMODITY, ENERGY);
        saveETF("217770", "TIGER 원유선물인버스(H)", COMMODITY, ENERGY);


        // ---------------------------------------------- 섹터별 ETF ----------------------------------------------

        // 섹터별 ETF - 전자부품 및 소재 (19개)
        saveETF("091160", "KODEX 반도체", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("091230", "TIGER 반도체", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("469150", "ACE AI반도체포커스", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS); // 이거 데이터 이상함...
        saveETF("494340", "ACE 글로벌AI맞춤형반도체", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("446770", "ACE 글로벌반도체TOP4 Plus SOLACTIVE", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("480040", "ACE 미국반도체데일리타겟커버드콜(합성)", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("469160", "ACE 일본반도체", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("466810", "BNK 2차전지양극재", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("305720", "KODEX 2차전지산업", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("461950", "KODEX 2차전지핵심소재10", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS); // 이거 데이터 이상함...
        saveETF("465330", "RISE 2차전지TOP10", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("422420", "RISE 2차전지액티브", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS); // 이거 데이터 이상함...
        saveETF("455860", "SOL 2차전지소부장Fn", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("429980", "SOL 한국형글로벌전기차&2차전지액티브", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("364980", "TIGER 2차전지TOP10", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("462010", "TIGER 2차전지소재Fn", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("305540", "TIGER 2차전지테마", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("414270", "ACE G2전기차&자율주행액티브", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);
        saveETF("394660", "TIGER 글로벌자율주행&전기차SOLACTIVE", SECTOR, ELECTRONIC_COMPONENTS_AND_MATERIALS);

        // 섹터별 ETF - IT/로봇 (17개)
        saveETF("266370", "KODEX IT", SECTOR, IT_ROBOT);
        saveETF("139260", "TIGER 200 IT", SECTOR, IT_ROBOT);
        saveETF("326240", "RISE IT플러스", SECTOR, IT_ROBOT);
        saveETF("261060", "TIGER 코스닥150IT", SECTOR, IT_ROBOT);
        saveETF("280320", "ACE 미국IT인터넷S&P(합성 H)", SECTOR, IT_ROBOT);
        saveETF("157490", "TIGER 소프트웨어", SECTOR, IT_ROBOT);
        saveETF("365000", "TIGER 인터넷TOP10", SECTOR, IT_ROBOT);
        saveETF("445290", "KODEX K-로봇액티브", SECTOR, IT_ROBOT);
        saveETF("276990", "KODEX 글로벌로봇(합성)", SECTOR, IT_ROBOT);
        saveETF("471040", "KoAct 글로벌AI&로봇액티브", SECTOR, IT_ROBOT);
        saveETF("469070", "RISE AI&로봇", SECTOR, IT_ROBOT);
        saveETF("456600", "TIMEFOLIO 글로벌AI인공지능액티브", SECTOR, IT_ROBOT);
        saveETF("470310", "UNICORN 생성형AI강소기업액티브", SECTOR, IT_ROBOT);
        saveETF("494340", "ACE 글로벌AI맞춤형반도체", SECTOR, IT_ROBOT);
        saveETF("487750", "BNK 온디바이스AI", SECTOR, IT_ROBOT);
        saveETF("418670", "TIGER 글로벌사이버보안INDXX", SECTOR, IT_ROBOT);
        saveETF("371450", "TIGER 글로벌클라우드컴퓨팅INDXX", SECTOR, IT_ROBOT);

        // 섹터별 ETF - 디지털 플랫폼 (7개)
        saveETF("368190", "HANARO Fn K-뉴딜디지털플러스", SECTOR, DIGITAL_PLATFORM);
        saveETF("368680", "KODEX K-뉴딜디지털플러스", SECTOR, DIGITAL_PLATFORM);
        saveETF("412770", "TIGER 글로벌메타버스액티브", SECTOR, DIGITAL_PLATFORM);
        saveETF("411050", "ACE 글로벌메타버스테크액티브", SECTOR, DIGITAL_PLATFORM);
        saveETF("402460", "HANARO Fn K-메타버스MZ", SECTOR, DIGITAL_PLATFORM);
        saveETF("419170", "HANARO 미국메타버스iSelect", SECTOR, DIGITAL_PLATFORM);
        saveETF("401470", "KODEX K-메타버스액티브", SECTOR, DIGITAL_PLATFORM);

        // 섹터별 ETF - 건설/조선 (6개)
        saveETF("117700", "KODEX 건설", SECTOR, CONSTRUCTION_SHIP);
        saveETF("139220", "TIGER 200 건설", SECTOR, CONSTRUCTION_SHIP);
        saveETF("441540", "HANARO Fn조선해운", SECTOR, CONSTRUCTION_SHIP);
        saveETF("445150", "KODEX K-친환경조선해운액티브", SECTOR, CONSTRUCTION_SHIP);
        saveETF("466920", "SOL 조선TOP3플러스", SECTOR, CONSTRUCTION_SHIP);
        saveETF("494670", "TIGER 조선TOP10", SECTOR, CONSTRUCTION_SHIP);

        // 섹터별 ETF - 친환경 (4개)
        saveETF("381570", "HANARO Fn친환경에너지", SECTOR, CONSTRUCTION_SHIP);
        saveETF("391600", "ACE 미국친환경그린테마INDXX", SECTOR, CONSTRUCTION_SHIP);
        saveETF("457990", "PLUS 태양광&ESS", SECTOR, CONSTRUCTION_SHIP);
        saveETF("413220", "SOL 차이나태양광CSI(합성)", SECTOR, CONSTRUCTION_SHIP);

        // 섹터별 ETF - 금융 (9개)
        saveETF("091170", "KODEX 은행", SECTOR, FINANCE);
        saveETF("140700", "KODEX 보험", SECTOR, FINANCE);
        saveETF("102970", "KODEX 증권", SECTOR, FINANCE);
        saveETF("139270", "RIGER 200 금융", SECTOR, FINANCE);
        saveETF("091220", "TIGER 은행", SECTOR, FINANCE);
        saveETF("157500", "TIGER 증권", SECTOR, FINANCE);
        saveETF("336160", "RISE 금융채액티브", SECTOR, FINANCE);
        saveETF("484880", "SOL 금융지주플러스고배당", SECTOR, FINANCE);
        saveETF("453650", "KODEX 미국S&P500금융", SECTOR, FINANCE);

        // 섹터별 ETF - 헬스케어 (7개)
        saveETF("453640", "KODEX 미국S&P500헬스케어", SECTOR, HEALTHCARE);
        saveETF("185680", "KODEX 미국 S&P바이오(합성)", SECTOR, HEALTHCARE);
        saveETF("266420", "KODEX 헬스케어", SECTOR, HEALTHCARE);
        saveETF("253280", "RISE 헬스케어", SECTOR, HEALTHCARE);
        saveETF("227540", "TIGER 200 헬스케어", SECTOR, HEALTHCARE);
        saveETF("261070", "TIGER 코스닥150바이오테크", SECTOR, HEALTHCARE);
        saveETF("143860", "TIGER 헬스케어", SECTOR, HEALTHCARE);

        // 섹터별 ETF - ESG (15개)
        saveETF("399110", "SOL 미국S&P500ESG", SECTOR, HEALTHCARE);
        saveETF("417630", "TIGER KEDI혁신기업ESG30", SECTOR, HEALTHCARE);
        saveETF("289260", "TIGER MSCI KOREA ESG리더스", SECTOR, HEALTHCARE);
        saveETF("289250", "TIGER MSCI KOREA ESG유니버설", SECTOR, HEALTHCARE);
        saveETF("413930", "WON AI ESG액티브", SECTOR, HEALTHCARE);
        saveETF("419890", "히어로즈 단기채권ESG액티브", SECTOR, HEALTHCARE);
        saveETF("385590", "ACE ESG액티브", SECTOR, HEALTHCARE);
        saveETF("285690", "FOCUS ESG리더스", SECTOR, HEALTHCARE);
        saveETF("337160", "KODEX 200ESG", SECTOR, HEALTHCARE);
        saveETF("439860", "KODEX ESG종합채권(A-이상)액티브", SECTOR, HEALTHCARE);
        saveETF("289040", "KODEX MSCI KOREA ESG유니버설", SECTOR, HEALTHCARE);
        saveETF("437080", "KODEX 미국종합채권ESG액티브(H)", SECTOR, HEALTHCARE);
        saveETF("395750", "PLUS ESG가치주액티브", SECTOR, HEALTHCARE);
        saveETF("395760", "PLUS ESG성장주액티브", SECTOR, HEALTHCARE);
        saveETF("290130", "RISE ESG사회책임투자", SECTOR, HEALTHCARE);


        // -------------------------------------- 고위험 ETF --------------------------------------

        // 고위험 ETF - 레버리지 (7개)
        saveETF("152500", "ACE 레버리지", HighLisk, LEVERAGE);
        saveETF("304780", "HANARO 200선물레버리지", HighLisk, LEVERAGE);
        saveETF("486780", "HANARO 200선물레버리지1.5X", HighLisk, LEVERAGE);
        saveETF("306530", "HANARO 코스닥150선물레버리지", HighLisk, LEVERAGE);
        saveETF("306950", "KODEX KRX300레버리지", HighLisk, LEVERAGE);
        saveETF("122630", "KODEX 레버리지", HighLisk, LEVERAGE);
        saveETF("409820", "미국나스닥100레버리지(합성 H)", HighLisk, LEVERAGE);

        // 고위험 ETF - 인버스 (10개)
        saveETF("465620", "ACE 미국 빅테크TOP7 Plus인버스(합성)", HighLisk, INVERSE);
        saveETF("145670", "ACE 인버스", HighLisk, INVERSE);
        saveETF("306520", "HANARO 200선물 인버스", HighLisk, INVERSE);
        saveETF("252670", "KODEX 200선물인버스2X", HighLisk, INVERSE);
        saveETF("176950", "KODEX 국채선물10년인버스", HighLisk, INVERSE);
        saveETF("292770", "KODEX 국채선물3년인버스", HighLisk, INVERSE);
        saveETF("261270", "KODEX 미국달러선물인버스", HighLisk, INVERSE);
        saveETF("261260", "KODEX 미국달러선물인버스2X", HighLisk, INVERSE);
        saveETF("114800", "KODEX 인버스", HighLisk, INVERSE);
        saveETF("251340", "KODEX 코스닥150선물인버스", HighLisk, INVERSE);
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

    // 시작시 etf 데이터 자동 초기화
    @Component
    @RequiredArgsConstructor
    public class DataInitializer implements CommandLineRunner {
        private final ETFInitService etfInitService;

        @Override
        public void run(String... args) throws Exception {
            etfInitService.initializeETFData();
            System.out.println("ETF 데이터 초기화 완료");
        }
    }

    public List<ETF> getAllETFs() {
        return etfRepository.findAll();
    }

}
