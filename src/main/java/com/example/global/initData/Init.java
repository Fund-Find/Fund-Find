package com.example.global.initData;

import com.example.domain.quizShow.entity.*;
import com.example.domain.quizShow.repository.QuizShowCategoryRepository;
import com.example.domain.quizShow.repository.QuizTypeRepository;
import com.example.domain.quizShow.request.QuizRequest;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.service.QuizShowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration  // 스프링 설정 클래스임을 나타냄
@Profile("dev") // 개발 환경에서만 실행되도록 설정
@Slf4j  // 로깅을 위한 어노테이션
public class Init {

    // 초기 데이터 생성을 위한 CommandLineRunner 빈 정의
    @Bean
    CommandLineRunner initData(
            QuizShowService quizShowService,
            QuizShowCategoryRepository categoryRepository,
            QuizTypeRepository quizTypeRepository
    ) {
        return args -> {
            log.info("초기 데이터 생성 시작...");

            try {
                // 퀴즈 타입, 카테고리, 퀴즈쇼 순차적 초기화
                initializeQuizTypes(quizTypeRepository);
                initializeCategories(categoryRepository);
                createQuizShows(quizShowService);

                log.info("초기 데이터 생성 완료");
            } catch (Exception e) {
                log.error("데이터 초기화 중 오류 발생", e);
                throw e;
            }
        };
    }

    // 퀴즈 타입 초기화 메소드
    private void initializeQuizTypes(QuizTypeRepository quizTypeRepository) {
        log.info("퀴즈 타입 초기화 중...");

        // 모든 퀴즈 타입에 대해 순회하며 생성
        for (QuizTypeEnum typeEnum : QuizTypeEnum.values()) {
            if (quizTypeRepository.findByType(typeEnum).isEmpty()) {
                QuizType quizType = QuizType.builder()
                        .type(typeEnum)
                        .typeName(typeEnum.getDescription())
                        .build();
                quizTypeRepository.save(quizType);
                log.debug("퀴즈 타입 생성: {}", typeEnum);
            }
        }
    }

    // 퀴즈 카테고리 초기화 메소드
    private void initializeCategories(QuizShowCategoryRepository categoryRepository) {
        log.info("카테고리 초기화 중...");

        // 모든 카테고리에 대해 순회하며 생성
        for (QuizShowCategoryEnum categoryEnum : QuizShowCategoryEnum.values()) {
            if (categoryRepository.findByCategoryName(categoryEnum.getDescription()).isEmpty()) {
                QuizShowCategory category = QuizShowCategory.builder()
                        .categoryName(categoryEnum.getDescription())
                        .build();
                categoryRepository.save(category);
                log.debug("카테고리 생성: {}", categoryEnum);
            }
        }
    }

    // 퀴즈쇼 생성 메소드
    private void createQuizShows(QuizShowService quizShowService) {
        log.info("퀴즈쇼 생성 중...");

        // 투자 퀴즈쇼 생성
        createQuizShow(
                quizShowService,
                "투자의 기초",
                QuizShowCategoryEnum.INVESTMENT,
                "투자의 기본 개념과 원칙을 배우는 입문자를 위한 퀴즈입니다.",
                createInvestmentQuizzes()
        );

        // 저축 퀴즈쇼 생성
        createQuizShow(
                quizShowService,
                "현명한 저축 방법",
                QuizShowCategoryEnum.SAVINGS,
                "효율적인 저축 전략과 방법을 배우는 퀴즈입니다.",
                createSavingMethodQuizzes()
        );

        // 주식 퀴즈쇼 생성
        createQuizShow(
                quizShowService,
                "주식투자 기초",
                QuizShowCategoryEnum.STOCK_MARKET,
                "주식시장의 기본 개념과 투자 방법을 배우는 퀴즈입니다.",
                createStockBasicsQuizzes()
        );

        // 부동산 퀴즈쇼
        createQuizShow(
                quizShowService,
                "부동산 투자의 이해",
                QuizShowCategoryEnum.REAL_ESTATE,
                "부동산 투자의 기본 개념과 주요 고려사항을 학습하는 퀴즈입니다.",
                createRealEstateQuizzes()
        );

        // 가상화폐 퀴즈쇼
        createQuizShow(
                quizShowService,
                "가상화폐와 블록체인",
                QuizShowCategoryEnum.CRYPTOCURRENCY,
                "가상화폐와 블록체인 기술의 기초를 이해하는 퀴즈입니다.",
                createCryptoQuizzes()
        );

        // 보험 퀴즈쇼
        createQuizShow(
                quizShowService,
                "보험의 기초",
                QuizShowCategoryEnum.INSURANCE,
                "보험의 기본 개념과 다양한 보험 상품을 이해하는 퀴즈입니다.",
                createInsuranceQuizzes()
        );
    }

    private void createQuizShow(
            QuizShowService quizShowService,
            String showName,
            QuizShowCategoryEnum category,
            String description,
            List<QuizRequest> quizzes) {

        QuizShowCreateRequest request = new QuizShowCreateRequest();
        request.setShowName(showName);
        request.setCategory(category);
        request.setShowDescription(description);
        request.setTotalQuizCount(quizzes.size());
        request.setTotalScore(quizzes.stream().mapToInt(QuizRequest::getQuizScore).sum());
        request.setQuizzes(quizzes);
        request.setUseCustomImage(false);

        try {
            quizShowService.create(request);
            log.debug("퀴즈쇼 생성 완료: {}", showName);
        } catch (Exception e) {
            log.error("퀴즈쇼 생성 중 오류 발생: " + showName, e);
        }
    }

    private List<QuizRequest> createInvestmentQuizzes() {
        List<QuizRequest> quizzes = new ArrayList<>();

        // 퀴즈 1: 분산투자
        QuizRequest quiz1 = new QuizRequest();
        quiz1.setQuizTypeId(1L); // MULTIPLE_CHOICE
        quiz1.setQuizContent("분산투자의 주요 목적은 무엇입니까?");
        quiz1.setQuizScore(10);
        List<QuizChoice> choices = new ArrayList<>();
        choices.add(QuizChoice.builder()
                .choiceContent("투자 위험 감소")
                .isCorrect(true)  // 정답 표시
                .build());
        choices.add(QuizChoice.builder()
                .choiceContent("수익률 극대화")
                .isCorrect(false)  // 정답 표시
                .build());
        choices.add(QuizChoice.builder()
                .choiceContent("단기 수익 실현")
                .isCorrect(false)  // 정답 표시
                .build());
        choices.add(QuizChoice.builder()
                .choiceContent("거래 비용 절감")
                .isCorrect(false)  // 정답 표시
                .build());
        quiz1.setChoices(choices); // 첫 번째 선택지가 정답
        quizzes.add(quiz1);

        // 퀴즈 2: 투자 기간
        QuizRequest quiz2 = new QuizRequest();
        quiz2.setQuizTypeId(1L); // MULTIPLE_CHOICE
        quiz2.setQuizContent("장기 투자의 장점으로 가장 적절한 것은?");
        quiz2.setQuizScore(20);
        List<QuizChoice> choices2 = new ArrayList<>();
        choices2.add(QuizChoice.builder()
                .choiceContent("시장 타이밍 포착 용이")
                .isCorrect(false)  // 정답 표시
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("복리효과 극대화")
                .isCorrect(true)  // 정답 표시
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("거래비용 증가")
                .isCorrect(false)  // 정답 표시
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("변동성 확대")
                .isCorrect(false)  // 정답 표시
                .build());
        quiz2.setChoices(choices2);
        quizzes.add(quiz2);

        // 퀴즈 3: OX 문제
        QuizRequest quiz3 = new QuizRequest();
        quiz3.setQuizTypeId(3L); // TRUE_FALSE
        quiz3.setQuizContent("PER이 낮을수록 기업의 실적 대비 주가가 저평가되어 있다.");
        quiz3.setQuizScore(15);
        List<QuizChoice> choices3 = new ArrayList<>();
        choices3.add(QuizChoice.builder()
                .choiceContent("T")
                .isCorrect(true)  // 정답 표시
                .build()); // 정답
        choices3.add(QuizChoice.builder()
                .choiceContent("F")
                .isCorrect(false)  // 정답 표시
                .build());
        quiz3.setChoices(choices3);
        quizzes.add(quiz3);

        // 퀴즈 4: 주관식
        QuizRequest quiz4 = new QuizRequest();
        quiz4.setQuizTypeId(2L); // SUBJECTIVE
        quiz4.setQuizContent("주식 투자에서 'ROE'의 영문 풀네임을 작성하시오.");
        quiz4.setQuizScore(25);
        List<QuizChoice> choices4 = new ArrayList<>();
        choices4.add(QuizChoice.builder()
                .choiceContent("Return On Equity")
                .isCorrect(true)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("Return on equity")
                .isCorrect(true)  // 대소문자 구분 없이 모두 정답 처리
                .build());
        quiz4.setChoices(choices4);
        quizzes.add(quiz4);

        // 퀴즈 5: 단답형
        QuizRequest quiz5 = new QuizRequest();
        quiz5.setQuizTypeId(4L); // SHORT_ANSWER
        quiz5.setQuizContent("주식시장에서 기업의 시가총액을 계산할 때 필요한 두 가지 요소는 '발행주식수'와 무엇인가?");
        quiz5.setQuizScore(30);
        List<QuizChoice> choices5 = new ArrayList<>();
        choices5.add(QuizChoice.builder()
                .choiceContent("주가")
                .isCorrect(true)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("주식가격")
                .isCorrect(true)  // 동의어도 정답으로 처리
                .build());
        quiz5.setChoices(choices5);
        quizzes.add(quiz5);

        return quizzes;
    }

    private List<QuizRequest> createSavingMethodQuizzes() {
        List<QuizRequest> quizzes = new ArrayList<>();

        // 퀴즈 1: 저축의 기본
        QuizRequest quiz1 = new QuizRequest();
        quiz1.setQuizTypeId(1L);
        quiz1.setQuizContent("다음 중 저축의 가장 중요한 원칙은?");
        quiz1.setQuizScore(10);
        List<QuizChoice> choices1 = new ArrayList<>();
        choices1.add(QuizChoice.builder()
                .choiceContent("정기적인 저축 습관")
                .isCorrect(true)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("고수익 상품 선택")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("단기 저축 위주")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("은행 상품만 이용")
                .isCorrect(false)
                .build());
        quiz1.setChoices(choices1);
        quizzes.add(quiz1);

        // 퀴즈 2: 복리 효과
        QuizRequest quiz2 = new QuizRequest();
        quiz2.setQuizTypeId(3L);
        quiz2.setQuizContent("복리 효과는 저축 기간이 길수록 더 큰 효과를 발휘한다.");
        quiz2.setQuizScore(10);
        List<QuizChoice> choices2 = new ArrayList<>();
        choices2.add(QuizChoice.builder()
                .choiceContent("TRUE")
                .isCorrect(true)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("FALSE")
                .isCorrect(false)
                .build());
        quiz2.setChoices(choices2);
        quizzes.add(quiz2);

        // 퀴즈 3: 예금자보호
        QuizRequest quiz3 = new QuizRequest();
        quiz3.setQuizTypeId(1L);
        quiz3.setQuizContent("예금자보호법상 보호되는 예금의 한도는 얼마인가?");
        quiz3.setQuizScore(10);
        List<QuizChoice> choices3 = new ArrayList<>();
        choices3.add(QuizChoice.builder()
                .choiceContent("5천만원")
                .isCorrect(true)
                .build());
        choices3.add(QuizChoice.builder()
                .choiceContent("3천만원")
                .isCorrect(false)
                .build());
        choices3.add(QuizChoice.builder()
                .choiceContent("1억원")
                .isCorrect(false)
                .build());
        choices3.add(QuizChoice.builder()
                .choiceContent("2억원")
                .isCorrect(false)
                .build());
        quiz3.setChoices(choices3);
        quizzes.add(quiz3);

        // 퀴즈 4: 저축 상품
        QuizRequest quiz4 = new QuizRequest();
        quiz4.setQuizTypeId(1L);
        quiz4.setQuizContent("목돈 마련을 위한 저축 상품으로 가장 적합한 것은?");
        quiz4.setQuizScore(10);
        List<QuizChoice> choices4 = new ArrayList<>();
        choices4.add(QuizChoice.builder()
                .choiceContent("정기적금")
                .isCorrect(true)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("보통예금")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("당좌예금")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("수시입출금통장")
                .isCorrect(false)
                .build());
        quiz4.setChoices(choices4);
        quizzes.add(quiz4);

        // 퀴즈 5: 금리 계산
        QuizRequest quiz5 = new QuizRequest();
        quiz5.setQuizTypeId(3L);
        quiz5.setQuizContent("단리 상품이 복리 상품보다 이자 수익이 항상 적다.");
        quiz5.setQuizScore(10);
        List<QuizChoice> choices5 = new ArrayList<>();
        choices5.add(QuizChoice.builder()
                .choiceContent("FALSE")
                .isCorrect(true)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("TRUE")
                .isCorrect(false)
                .build());
        quiz5.setChoices(choices5);
        quizzes.add(quiz5);

        return quizzes;
    }

    private List<QuizRequest> createStockBasicsQuizzes() {
        List<QuizRequest> quizzes = new ArrayList<>();

        // 퀴즈 1: 주식 기초
        QuizRequest quiz1 = new QuizRequest();
        quiz1.setQuizTypeId(1L);
        quiz1.setQuizContent("주식시장에서 '시가총액'이란 무엇을 의미하는가?");
        quiz1.setQuizScore(10);
        List<QuizChoice> choices1 = new ArrayList<>();
        choices1.add(QuizChoice.builder()
                .choiceContent("발행주식 수 × 주가")
                .isCorrect(true)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("하루 거래량 × 주가")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("순이익 × 주가")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("자본금 × 주가")
                .isCorrect(false)
                .build());
        quiz1.setChoices(choices1);
        quizzes.add(quiz1);

        // 퀴즈 2: 주가 지표
        QuizRequest quiz2 = new QuizRequest();
        quiz2.setQuizTypeId(1L);
        quiz2.setQuizContent("주가수익비율(PER)이 낮은 주식의 특징으로 가장 적절한 것은?");
        quiz2.setQuizScore(10);
        List<QuizChoice> choices2 = new ArrayList<>();
        choices2.add(QuizChoice.builder()
                .choiceContent("주가가 기업 실적에 비해 저평가되어 있다")
                .isCorrect(true)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("기업의 성장성이 매우 높다")
                .isCorrect(false)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("배당 수익률이 높다")
                .isCorrect(false)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("기업의 부채비율이 낮다")
                .isCorrect(false)
                .build());
        quiz2.setChoices(choices2);
        quizzes.add(quiz2);

        // 퀴즈 3: 배당
        QuizRequest quiz3 = new QuizRequest();
        quiz3.setQuizTypeId(3L);
        quiz3.setQuizContent("배당락일 이후에 주식을 매수하면 해당 회차의 배당금을 받을 수 있다.");
        quiz3.setQuizScore(10);
        List<QuizChoice> choices3 = new ArrayList<>();
        choices3.add(QuizChoice.builder()
                .choiceContent("FALSE")
                .isCorrect(true)
                .build());
        choices3.add(QuizChoice.builder()
                .choiceContent("TRUE")
                .isCorrect(false)
                .build());
        quiz3.setChoices(choices3);
        quizzes.add(quiz3);

        // 퀴즈 4: 매매 기법
        QuizRequest quiz4 = new QuizRequest();
        quiz4.setQuizTypeId(1L);
        quiz4.setQuizContent("다음 중 '분산투자'의 주요 목적은?");
        quiz4.setQuizScore(10);
        List<QuizChoice> choices4 = new ArrayList<>();
        choices4.add(QuizChoice.builder()
                .choiceContent("투자 위험의 감소")
                .isCorrect(true)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("투자 수익의 극대화")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("거래비용의 절감")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("단기 수익의 실현")
                .isCorrect(false)
                .build());
        quiz4.setChoices(choices4);
        quizzes.add(quiz4);

        // 퀴즈 5: 주식 용어
        QuizRequest quiz5 = new QuizRequest();
        quiz5.setQuizTypeId(1L);
        quiz5.setQuizContent("다음 중 '거래정지'의 사유가 아닌 것은?");
        quiz5.setQuizScore(10);
        List<QuizChoice> choices5 = new ArrayList<>();
        choices5.add(QuizChoice.builder()
                .choiceContent("주가가 연일 상승하는 경우")
                .isCorrect(true)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("상장폐지 실질심사 사유 발생")
                .isCorrect(false)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("부도 발생")
                .isCorrect(false)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("회생절차 개시 신청")
                .isCorrect(false)
                .build());
        quiz5.setChoices(choices5);
        quizzes.add(quiz5);

        return quizzes;
    }

    private List<QuizRequest> createRealEstateQuizzes() {
        List<QuizRequest> quizzes = new ArrayList<>();

        // 퀴즈 1: 부동산 투자 기초
        QuizRequest quiz1 = new QuizRequest();
        quiz1.setQuizTypeId(1L);
        quiz1.setQuizContent("부동산 투자에서 가장 중요한 고려사항은?");
        quiz1.setQuizScore(10);
        List<QuizChoice> choices1 = new ArrayList<>();
        choices1.add(QuizChoice.builder()
                .choiceContent("입지조건")
                .isCorrect(true)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("건물의 외관")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("건물의 연식")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("주차장 크기")
                .isCorrect(false)
                .build());
        quiz1.setChoices(choices1);
        quizzes.add(quiz1);

        // 퀴즈 2: 부동산 용어
        QuizRequest quiz2 = new QuizRequest();
        quiz2.setQuizTypeId(1L);
        quiz2.setQuizContent("다음 중 '경매'에 대한 설명으로 올바른 것은?");
        quiz2.setQuizScore(10);
        List<QuizChoice> choices2 = new ArrayList<>();
        choices2.add(QuizChoice.builder()
                .choiceContent("법원의 주관 하에 진행되는 강제매각절차")
                .isCorrect(true)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("중개인을 통한 매매")
                .isCorrect(false)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("개인 간 직거래")
                .isCorrect(false)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("임대차 계약")
                .isCorrect(false)
                .build());
        quiz2.setChoices(choices2);
        quizzes.add(quiz2);

        // 퀴즈 3: 부동산 세금
        QuizRequest quiz3 = new QuizRequest();
        quiz3.setQuizTypeId(3L);
        quiz3.setQuizContent("다주택자가 보유한 주택에는 일반적으로 더 높은 보유세가 부과된다.");
        quiz3.setQuizScore(10);
        List<QuizChoice> choices3 = new ArrayList<>();
        choices3.add(QuizChoice.builder()
                .choiceContent("TRUE")
                .isCorrect(true)
                .build());
        choices3.add(QuizChoice.builder()
                .choiceContent("FALSE")
                .isCorrect(false)
                .build());
        quiz3.setChoices(choices3);
        quizzes.add(quiz3);

        // 퀴즈 4: 주택담보대출
        QuizRequest quiz4 = new QuizRequest();
        quiz4.setQuizTypeId(1L);
        quiz4.setQuizContent("주택담보대출의 LTV(담보인정비율)가 의미하는 것은?");
        quiz4.setQuizScore(10);
        List<QuizChoice> choices4 = new ArrayList<>();
        choices4.add(QuizChoice.builder()
                .choiceContent("주택가격 대비 최대 대출가능 비율")
                .isCorrect(true)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("연간 소득 대비 대출금액")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("월 소득 대비 원리금상환액")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("대출금액 대비 이자비용")
                .isCorrect(false)
                .build());
        quiz4.setChoices(choices4);
        quizzes.add(quiz4);

        // 퀴즈 5: 임대차계약
        QuizRequest quiz5 = new QuizRequest();
        quiz5.setQuizTypeId(1L);
        quiz5.setQuizContent("전세계약 시 가장 중요한 확인사항은?");
        quiz5.setQuizScore(10);
        List<QuizChoice> choices5 = new ArrayList<>();
        choices5.add(QuizChoice.builder()
                .choiceContent("선순위 권리관계")
                .isCorrect(true)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("방의 개수")
                .isCorrect(false)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("주차공간")
                .isCorrect(false)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("도배상태")
                .isCorrect(false)
                .build());
        quiz5.setChoices(choices5);
        quizzes.add(quiz5);

        return quizzes;
    }

    private List<QuizRequest> createCryptoQuizzes() {
        List<QuizRequest> quizzes = new ArrayList<>();

        // 퀴즈 1: 블록체인 기초
        QuizRequest quiz1 = new QuizRequest();
        quiz1.setQuizTypeId(1L);
        quiz1.setQuizContent("블록체인의 핵심 특징이 아닌 것은?");
        quiz1.setQuizScore(10);
        List<QuizChoice> choices1 = new ArrayList<>();
        choices1.add(QuizChoice.builder()
                .choiceContent("중앙 집중화")
                .isCorrect(true)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("투명성")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("불변성")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("탈중앙화")
                .isCorrect(false)
                .build());
        quiz1.setChoices(choices1);
        quizzes.add(quiz1);

        // 퀴즈 2: 가상화폐 개념
        QuizRequest quiz2 = new QuizRequest();
        quiz2.setQuizTypeId(1L);
        quiz2.setQuizContent("비트코인의 발행량 한도는?");
        quiz2.setQuizScore(10);
        List<QuizChoice> choices2 = new ArrayList<>();
        choices2.add(QuizChoice.builder()
                .choiceContent("2,100만 개")
                .isCorrect(true)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("1,000만 개")
                .isCorrect(false)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("무제한")
                .isCorrect(false)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("5,000만 개")
                .isCorrect(false)
                .build());
        quiz2.setChoices(choices2);
        quizzes.add(quiz2);

        // 퀴즈 3: 채굴
        QuizRequest quiz3 = new QuizRequest();
        quiz3.setQuizTypeId(3L);
        quiz3.setQuizContent("비트코인 채굴은 새로운 거래를 검증하고 블록체인에 기록하는 과정이다.");
        quiz3.setQuizScore(10);
        List<QuizChoice> choices3 = new ArrayList<>();
        choices3.add(QuizChoice.builder()
                .choiceContent("TRUE")
                .isCorrect(true)
                .build());
        choices3.add(QuizChoice.builder()
                .choiceContent("FALSE")
                .isCorrect(false)
                .build());
        quiz3.setChoices(choices3);
        quizzes.add(quiz3);

        // 퀴즈 4: 지갑
        QuizRequest quiz4 = new QuizRequest();
        quiz4.setQuizTypeId(1L);
        quiz4.setQuizContent("가상화폐 거래에서 '콜드월렛'의 특징은?");
        quiz4.setQuizScore(10);
        List<QuizChoice> choices4 = new ArrayList<>();
        choices4.add(QuizChoice.builder()
                .choiceContent("인터넷에 연결되지 않은 오프라인 지갑")
                .isCorrect(true)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("온라인 거래소의 지갑")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("스마트폰 앱 지갑")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("웹 브라우저 지갑")
                .isCorrect(false)
                .build());
        quiz4.setChoices(choices4);
        quizzes.add(quiz4);

        // 퀴즈 5: 보안
        QuizRequest quiz5 = new QuizRequest();
        quiz5.setQuizTypeId(1L);
        quiz5.setQuizContent("가상화폐 거래 시 가장 중요한 보안 수칙은?");
        quiz5.setQuizScore(10);
        List<QuizChoice> choices5 = new ArrayList<>();
        choices5.add(QuizChoice.builder()
                .choiceContent("개인키의 안전한 보관")
                .isCorrect(true)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("공개키 공유")
                .isCorrect(false)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("거래내역 공개")
                .isCorrect(false)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("익명성 유지")
                .isCorrect(false)
                .build());
        quiz5.setChoices(choices5);
        quizzes.add(quiz5);

        return quizzes;
    }

    private List<QuizRequest> createInsuranceQuizzes() {
        List<QuizRequest> quizzes = new ArrayList<>();

        // 퀴즈 1: 보험 기초
        QuizRequest quiz1 = new QuizRequest();
        quiz1.setQuizTypeId(1L);
        quiz1.setQuizContent("보험의 기본 원리가 아닌 것은?");
        quiz1.setQuizScore(10);
        List<QuizChoice> choices1 = new ArrayList<>();
        choices1.add(QuizChoice.builder()
                .choiceContent("투기성")
                .isCorrect(true)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("대수의 법칙")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("위험의 분산")
                .isCorrect(false)
                .build());
        choices1.add(QuizChoice.builder()
                .choiceContent("보험료 납입")
                .isCorrect(false)
                .build());
        quiz1.setChoices(choices1);
        quizzes.add(quiz1);

        // 퀴즈 2: 보험 종류
        QuizRequest quiz2 = new QuizRequest();
        quiz2.setQuizTypeId(1L);
        quiz2.setQuizContent("다음 중 제3보험에 해당하는 것은?");
        quiz2.setQuizScore(10);
        List<QuizChoice> choices2 = new ArrayList<>();
        choices2.add(QuizChoice.builder()
                .choiceContent("실손의료보험")
                .isCorrect(true)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("자동차보험")
                .isCorrect(false)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("화재보험")
                .isCorrect(false)
                .build());
        choices2.add(QuizChoice.builder()
                .choiceContent("종신보험")
                .isCorrect(false)
                .build());
        quiz2.setChoices(choices2);
        quizzes.add(quiz2);

        // 퀴즈 3: 보험금 청구
        QuizRequest quiz3 = new QuizRequest();
        quiz3.setQuizTypeId(3L);
        quiz3.setQuizContent("보험금 청구는 보험사고 발생일로부터 3년 이내에 해야 한다.");
        quiz3.setQuizScore(10);
        List<QuizChoice> choices3 = new ArrayList<>();
        choices3.add(QuizChoice.builder()
                .choiceContent("TRUE")
                .isCorrect(true)
                .build());
        choices3.add(QuizChoice.builder()
                .choiceContent("FALSE")
                .isCorrect(false)
                .build());
        quiz3.setChoices(choices3);
        quizzes.add(quiz3);

        // 퀴즈 4: 보험료 계산
        QuizRequest quiz4 = new QuizRequest();
        quiz4.setQuizTypeId(1L);
        quiz4.setQuizContent("보험료 산정에 영향을 미치지 않는 요소는?");
        quiz4.setQuizScore(10);
        List<QuizChoice> choices4 = new ArrayList<>();
        choices4.add(QuizChoice.builder()
                .choiceContent("취미 생활")
                .isCorrect(true)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("연령")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("직업")
                .isCorrect(false)
                .build());
        choices4.add(QuizChoice.builder()
                .choiceContent("건강 상태")
                .isCorrect(false)
                .build());
        quiz4.setChoices(choices4);
        quizzes.add(quiz4);

        // 퀴즈 5: 보험 계약
        QuizRequest quiz5 = new QuizRequest();
        quiz5.setQuizTypeId(1L);
        quiz5.setQuizContent("보험계약의 특성이 아닌 것은?");
        quiz5.setQuizScore(10);
        List<QuizChoice> choices5 = new ArrayList<>();
        choices5.add(QuizChoice.builder()
                .choiceContent("즉시 해지 가능")
                .isCorrect(true)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("선의계약")
                .isCorrect(false)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("조건부계약")
                .isCorrect(false)
                .build());
        choices5.add(QuizChoice.builder()
                .choiceContent("불요식계약")
                .isCorrect(false)
                .build());
        quiz5.setChoices(choices5);
        quizzes.add(quiz5);

        return quizzes;
    }
}