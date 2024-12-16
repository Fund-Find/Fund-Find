//package com.example.global.initData;
//
//import com.example.domain.quizShow.entity.QuizShowCategory;
//import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
//import com.example.domain.quizShow.entity.QuizType;
//import com.example.domain.quizShow.entity.QuizTypeEnum;
//import com.example.domain.quizShow.repository.QuizShowCategoryRepository;
//import com.example.domain.quizShow.repository.QuizTypeRepository;
//import com.example.domain.quizShow.request.QuizRequest;
//import com.example.domain.quizShow.request.QuizShowCreateRequest;
//import com.example.domain.quizShow.service.QuizShowService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.env.Environment;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration  // 스프링 설정 클래스임을 나타냄
//@Profile("dev") // 개발 환경에서만 실행되도록 설정
//@Slf4j  // 로깅을 위한 어노테이션
//public class Init {
//
//    // 초기 데이터 생성을 위한 CommandLineRunner 빈 정의
//    @Bean
//    CommandLineRunner initData(
//            QuizShowService quizShowService,
//            QuizShowCategoryRepository categoryRepository,
//            QuizTypeRepository quizTypeRepository,
//            Environment environment
//    ) {
//        return args -> {
//            log.info("초기 데이터 생성 시작...");
//
//            try {
//                // 퀴즈 타입, 카테고리, 퀴즈쇼 순차적 초기화
//                initializeQuizTypes(quizTypeRepository);
//                initializeCategories(categoryRepository);
//                createQuizShows(quizShowService);
//
//                log.info("초기 데이터 생성 완료");
//            } catch (Exception e) {
//                log.error("데이터 초기화 중 오류 발생", e);
//                throw e;
//            }
//        };
//    }
//
//    // 퀴즈 타입 초기화 메소드
//    private void initializeQuizTypes(QuizTypeRepository quizTypeRepository) {
//        log.info("퀴즈 타입 초기화 중...");
//
//        // 모든 퀴즈 타입에 대해 순회하며 생성
//        for (QuizTypeEnum typeEnum : QuizTypeEnum.values()) {
//            if (quizTypeRepository.findByType(typeEnum).isEmpty()) {
//                QuizType quizType = QuizType.builder()
//                        .type(typeEnum)
//                        .typeName(typeEnum.getDescription())
//                        .build();
//                quizTypeRepository.save(quizType);
//                log.debug("퀴즈 타입 생성: {}", typeEnum);
//            }
//        }
//    }
//
//    // 퀴즈 카테고리 초기화 메소드
//    private void initializeCategories(QuizShowCategoryRepository categoryRepository) {
//        log.info("카테고리 초기화 중...");
//
//        // 모든 카테고리에 대해 순회하며 생성
//        for (QuizShowCategoryEnum categoryEnum : QuizShowCategoryEnum.values()) {
//            if (categoryRepository.findByCategoryName(categoryEnum.getDescription()).isEmpty()) {
//                QuizShowCategory category = QuizShowCategory.builder()
//                        .categoryName(categoryEnum.getDescription())
//                        .build();
//                categoryRepository.save(category);
//                log.debug("카테고리 생성: {}", categoryEnum);
//            }
//        }
//    }
//
//    // 퀴즈쇼 생성 메소드
//    private void createQuizShows(QuizShowService quizShowService) {
//        log.info("퀴즈쇼 생성 중...");
//
//        // 모든 카테고리별 퀴즈쇼 생성
//        for (QuizShowCategoryEnum category : QuizShowCategoryEnum.values()) {
//            createQuizShowsForCategory(category, quizShowService);
//        }
//    }
//
//    // 카테고리별 퀴즈쇼 생성 메소드
//    private void createQuizShowsForCategory(QuizShowCategoryEnum category, QuizShowService quizShowService) {
//        // 카테고리별 퀴즈쇼 템플릿 목록
//        List<QuizShowTemplate> templates = getCategoryTemplates(category);
//
//        for (QuizShowTemplate template : templates) {
//            try {
//                QuizShowCreateRequest request = createQuizShowRequest(category, template);
//                quizShowService.create(request);
//                log.debug("퀴즈쇼 생성 완료: {}", template.getTitle());
//            } catch (Exception e) {
//                log.error("퀴즈쇼 생성 중 오류 발생: " + template.getTitle(), e);
//            }
//        }
//    }
//
//    // 퀴즈쇼 생성 요청 객체 생성 메소드
//    private QuizShowCreateRequest createQuizShowRequest(QuizShowCategoryEnum category, QuizShowTemplate template) {
//        QuizShowCreateRequest request = new QuizShowCreateRequest();
//        request.setShowName(template.getTitle());
//        request.setCategory(category);
//        request.setShowDescription(template.getDescription());
//        request.setTotalQuizCount(5); // 기본 퀴즈 개수
//        request.setTotalScore(50);    // 기본 총점
//        request.setUseCustomImage(false);
//        request.setQuizzes(template.getQuizzes());
//
//        return request;
//    }
//
//    // 퀴즈쇼 템플릿 클래스 정의
//    @lombok.Value
//    private static class QuizShowTemplate {
//        String title;
//        String description;
//        List<QuizRequest> quizzes;
//    }
//
//    // 카테고리별 템플릿 생성 메소드
//    private List<QuizShowTemplate> getCategoryTemplates(QuizShowCategoryEnum category) {
//        switch (category) {
//            case INVESTMENT:
//                return createInvestmentTemplates();
//            case SAVINGS:
//                return createSavingsTemplates();
//            case STOCK_MARKET:
//                return createStockMarketTemplates();
//            case REAL_ESTATE:
//                return createRealEstateTemplates();
//            case CRYPTOCURRENCY:
//                return createCryptoTemplates();
//            case INSURANCE:
//                return createInsuranceTemplates();
//            default:
//                return new ArrayList<>();
//        }
//    }
//
//    // 투자 카테고리 템플릿 생성
//    private List<QuizShowTemplate> createInvestmentTemplates() {
//        return Arrays.asList(
//                new QuizShowTemplate(
//                        "투자의 기초",
//                        "투자의 기본 개념과 원칙을 배우는 입문자를 위한 퀴즈입니다.",
//                        createInvestmentQuizzes()
//                ),
//                new QuizShowTemplate(
//                        "글로벌 투자 전략",
//                        "국제 금융 시장과 해외 투자 전략을 다루는 심화 퀴즈입니다.",
//                        createGlobalInvestmentQuizzes()
//                ),
//                new QuizShowTemplate(
//                        "위험관리와 투자",
//                        "투자 위험을 관리하는 방법을 배우는 퀴즈입니다.",
//                        createRiskManagementQuizzes()
//                )
//        );
//    }
//
//    // 저축 카테고리 템플릿 생성
//    private List<QuizShowTemplate> createSavingsTemplates() {
//        return Arrays.asList(
//                new QuizShowTemplate(
//                        "현명한 저축 방법",
//                        "효율적인 저축 전략과 방법을 배우는 퀴즈입니다.",
//                        createSavingMethodQuizzes()
//                ),
//                new QuizShowTemplate(
//                        "생활 속 저축 노하우",
//                        "일상생활에서 실천할 수 있는 저축 방법을 배우는 퀴즈입니다.",
//                        createDailySavingQuizzes()
//                )
//        );
//    }
//
//    // 주식시장 카테고리 템플릿 생성
//    private List<QuizShowTemplate> createStockMarketTemplates() {
//        return Arrays.asList(
//                new QuizShowTemplate(
//                        "주식투자 기초",
//                        "주식시장의 기본 개념과 투자 방법을 배우는 퀴즈입니다.",
//                        createStockBasicsQuizzes()
//                ),
//                new QuizShowTemplate(
//                        "기술적 분석",
//                        "주식 차트 분석과 기술적 지표를 배우는 퀴즈입니다.",
//                        createTechnicalAnalysisQuizzes()
//                )
//        );
//    }
//
//    // 퀴즈 생성 메소드들
//    private List<QuizRequest> createInvestmentQuizzes() {
//        List<QuizRequest> quizzes = new ArrayList<>();
//
//        // 퀴즈 1: 분산투자
//        QuizRequest quiz1 = new QuizRequest();
//        quiz1.setQuizTypeId(1L); // MULTIPLE_CHOICE 타입
//        quiz1.setQuizContent("분산투자의 주요 목적은 무엇입니까?");
//        quiz1.setQuizScore(10);
//        quiz1.setChoices(Arrays.asList(
//                "투자 위험 감소",
//                "수익률 극대화",
//                "단기 수익 실현",
//                "거래 비용 절감"
//        ));
//        quizzes.add(quiz1);
//
//        // 퀴즈 2: 투자 기간
//        QuizRequest quiz2 = new QuizRequest();
//        quiz2.setQuizTypeId(1L);
//        quiz2.setQuizContent("장기 투자의 장점으로 가장 적절한 것은?");
//        quiz2.setQuizScore(10);
//        quiz2.setChoices(Arrays.asList(
//                "복리효과 극대화",
//                "시장 타이밍 포착 용이",
//                "거래비용 증가",
//                "변동성 확대"
//        ));
//        quizzes.add(quiz2);
//
//        // 퀴즈 3: 투자 전략
//        QuizRequest quiz3 = new QuizRequest();
//        quiz3.setQuizTypeId(1L);
//        quiz3.setQuizContent("다음 중 가장 안전한 투자 전략은?");
//        quiz3.setQuizScore(10);
//        quiz3.setChoices(Arrays.asList(
//                "정기적인 분할 투자",
//                "전액 일시 투자",
//                "레버리지 활용",
//                "단기 매매"
//        ));
//        quizzes.add(quiz3);
//
//        return quizzes;
//    }
//
//    // 추가 퀴즈 생성 메소드들은 비슷한 패턴으로 구현
//    private List<QuizRequest> createGlobalInvestmentQuizzes() {
//        // 글로벌 투자 관련 퀴즈들 구현
//        return new ArrayList<>();
//    }
//
//    private List<QuizRequest> createRiskManagementQuizzes() {
//        // 위험관리 관련 퀴즈들 구현
//        return new ArrayList<>();
//    }
//
//    private List<QuizRequest> createSavingMethodQuizzes() {
//        // 저축 방법 관련 퀴즈들 구현
//        return new ArrayList<>();
//    }
//
//    private List<QuizRequest> createDailySavingQuizzes() {
//        // 일상 저축 관련 퀴즈들 구현
//        return new ArrayList<>();
//    }
//
//    private List<QuizRequest> createStockBasicsQuizzes() {
//        // 주식 기초 관련 퀴즈들 구현
//        return new ArrayList<>();
//    }
//
//    private List<QuizRequest> createTechnicalAnalysisQuizzes() {
//        // 기술적 분석 관련 퀴즈들 구현
//        return new ArrayList<>();
//    }
//
//    // 나머지 카테고리 템플릿 생성 메소드들
//    private List<QuizShowTemplate> createRealEstateTemplates() {
//        // 부동산 관련 템플릿 구현
//        return new ArrayList<>();
//    }
//
//    private List<QuizShowTemplate> createCryptoTemplates() {
//        // 가상화폐 관련 템플릿 구현
//        return new ArrayList<>();
//    }
//
//    private List<QuizShowTemplate> createInsuranceTemplates() {
//        // 보험 관련 템플릿 구현
//        return new ArrayList<>();
//    }
//}