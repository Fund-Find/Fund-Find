package com.example.global.initData;

import com.example.domain.quizShow.entity.QuizShowCategory;
import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import com.example.domain.quizShow.entity.QuizType;
import com.example.domain.quizShow.entity.QuizTypeEnum;
import com.example.domain.quizShow.repository.QuizShowCategoryRepository;
import com.example.domain.quizShow.repository.QuizTypeRepository;
import com.example.domain.quizShow.request.QuizRequest;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.service.QuizShowService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.*;

@Configuration
@Profile("dev")
public class Init {
    @Bean
    CommandLineRunner initData(QuizShowService quizShowService,
                               QuizShowCategoryRepository categoryRepository,
                               QuizTypeRepository quizTypeRepository) {
        return args -> {
            // 1. QuizType 생성
            Arrays.stream(QuizTypeEnum.values())
                    .forEach(typeEnum -> {
                        if (quizTypeRepository.findByType(typeEnum).isEmpty()) {
                            QuizType quizType = QuizType.builder()
                                    .type(typeEnum)
                                    .typeName(typeEnum.getDescription())
                                    .build();
                            quizTypeRepository.save(quizType);
                        }
                    });
            // 카테고리 생성
            Arrays.stream(QuizShowCategoryEnum.values())
                    .forEach(categoryEnum -> {
                        // 이미 존재하는 카테고리인지 확인
                        if (categoryRepository.findByCategoryName(categoryEnum.getDescription()).isEmpty()) {
                            QuizShowCategory category = QuizShowCategory.builder()
                                    .categoryName(categoryEnum.getDescription())
                                    .build();
                            categoryRepository.save(category);
                        }
                    });

            List<String> investmentTitles = Arrays.asList(
                    "초보자를 위한 투자 기초", "전문가의 투자 전략", "글로벌 투자 트렌드",
                    "투자 리스크 관리", "장기 투자의 비결"
            );

            List<String> savingsTitles = Arrays.asList(
                    "똑똑한 저축 방법", "재테크의 시작", "알뜰 저축 비법",
                    "생활 속 저축 팁", "청년을 위한 저축 전략"
            );

            List<String> stockMarketTitles = Arrays.asList(
                    "주식시장 입문", "차트 분석의 기술", "기업 가치 평가",
                    "테마주 투자 전략", "배당주 투자"
            );

            List<String> realEstateTitles = Arrays.asList(
                    "부동산 투자의 정석", "상가투자 노하우", "아파트 투자 전략",
                    "부동산 시장 분석", "임대사업 성공비법"
            );

            List<String> cryptoTitles = Arrays.asList(
                    "가상화폐 시작하기", "알트코인 분석", "NFT 투자 가이드",
                    "블록체인 기술 이해", "디파이 투자 전략"
            );

            List<String> insuranceTitles = Arrays.asList(
                    "보험의 기초", "실비보험 가이드", "종신보험 완전정복",
                    "보험 청구 노하우", "보험 상품 비교"
            );

            Map<QuizShowCategoryEnum, List<String>> categoryTitles = new HashMap<>();
            categoryTitles.put(QuizShowCategoryEnum.INVESTMENT, investmentTitles);
            categoryTitles.put(QuizShowCategoryEnum.SAVINGS, savingsTitles);
            categoryTitles.put(QuizShowCategoryEnum.STOCK_MARKET, stockMarketTitles);
            categoryTitles.put(QuizShowCategoryEnum.REAL_ESTATE, realEstateTitles);
            categoryTitles.put(QuizShowCategoryEnum.CRYPTOCURRENCY, cryptoTitles);
            categoryTitles.put(QuizShowCategoryEnum.INSURANCE, insuranceTitles);

            int quizShowCount = 0;
            for (Map.Entry<QuizShowCategoryEnum, List<String>> entry : categoryTitles.entrySet()) {
                QuizShowCategoryEnum category = entry.getKey();
                List<String> titles = entry.getValue();

                QuizShowCategory savedCategory = categoryRepository.findByCategoryName(category.getDescription())
                        .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다: " + category));

                for (String title : titles) {
                    QuizShowCreateRequest request = new QuizShowCreateRequest();
                    request.setShowName(title);
                    request.setCategory(category);
                    request.setShowDescription(generateDescription(title, category));
                    request.setTotalQuizCount(3);
                    request.setTotalScore(30);
                    request.setUseCustomImage(false);

                    List<QuizRequest> quizzes = generateQuizzes(savedCategory.getId(), category);
                    request.setQuizzes(quizzes);

                    quizShowService.create(request);
                    quizShowCount++;

                    if (quizShowCount >= 30) break;
                }
                if (quizShowCount >= 30) break;
            }
        };
    }

    private String generateDescription(String title, QuizShowCategoryEnum category) {
        return String.format("%s 분야의 %s에 대해 알아보는 퀴즈입니다. 기초부터 심화까지 다양한 문제로 구성되어 있습니다.",
                category.getDescription(), title);
    }

    private List<QuizRequest> generateQuizzes(Long categoryId, QuizShowCategoryEnum category) {
        List<QuizRequest> quizzes = new ArrayList<>();

        switch (category) {
            case INVESTMENT:
                quizzes.add(createQuiz(categoryId, "투자에서 '분산투자'란 무엇인가요?", 10,
                        Arrays.asList("여러 자산에 나누어 투자", "한 곳에 집중투자", "정기적으로 투자")));
                quizzes.add(createQuiz(categoryId, "장기투자의 장점은 무엇인가요?", 10,
                        Arrays.asList("복리효과", "단기수익", "높은 변동성")));
                quizzes.add(createQuiz(categoryId, "투자위험을 줄이는 방법은?", 10,
                        Arrays.asList("정보수집과 분석", "감정적 대응", "대출을 통한 투자")));
                break;

            case SAVINGS:
                quizzes.add(createQuiz(categoryId, "적금과 예금의 차이점은?", 10,
                        Arrays.asList("납입방식", "이자율", "가입기간")));
                quizzes.add(createQuiz(categoryId, "비상자금은 몇 개월치가 적정한가요?", 10,
                        Arrays.asList("3-6개월", "1개월", "12개월")));
                quizzes.add(createQuiz(categoryId, "저축의 첫 단계로 가장 중요한 것은?", 10,
                        Arrays.asList("지출계획 수립", "높은 수익률", "대출상품 비교")));
                break;

            case STOCK_MARKET:
                quizzes.add(createQuiz(categoryId, "PER이란 무엇인가요?", 10,
                        Arrays.asList("주가수익비율", "주당순이익", "주가순자산비율")));
                quizzes.add(createQuiz(categoryId, "배당수익률이란?", 10,
                        Arrays.asList("주당배당금/주가", "순이익/자본", "부채/자산")));
                quizzes.add(createQuiz(categoryId, "우선주의 특징은?", 10,
                        Arrays.asList("의결권 제한", "높은 변동성", "없음")));
                break;

            case REAL_ESTATE:
                quizzes.add(createQuiz(categoryId, "청약통장의 가입조건은?", 10,
                        Arrays.asList("만 19세 이상", "만 20세 이상", "만 21세 이상")));
                quizzes.add(createQuiz(categoryId, "공시지가란?", 10,
                        Arrays.asList("국가가 산정한 토지가격", "시장거래가격", "은행평가가격")));
                quizzes.add(createQuiz(categoryId, "분양권 전매제한이란?", 10,
                        Arrays.asList("일정기간 거래금지", "가격제한", "지역제한")));
                break;

            case CRYPTOCURRENCY:
                quizzes.add(createQuiz(categoryId, "블록체인의 핵심 특징은?", 10,
                        Arrays.asList("분산원장", "중앙집중", "단일거래")));
                quizzes.add(createQuiz(categoryId, "코인 지갑의 종류는?", 10,
                        Arrays.asList("핫월렛과 콜드월렛", "온라인월렛", "모바일월렛")));
                quizzes.add(createQuiz(categoryId, "채굴이란 무엇인가요?", 10,
                        Arrays.asList("작업증명", "거래증명", "보유증명")));
                break;

            case INSURANCE:
                quizzes.add(createQuiz(categoryId, "보험료와 보험금의 차이는?", 10,
                        Arrays.asList("납입금액과 수령금액", "동일한 개념", "선택사항")));
                quizzes.add(createQuiz(categoryId, "실손보험의 특징은?", 10,
                        Arrays.asList("실제 발생 의료비", "정액보상", "부분보상")));
                quizzes.add(createQuiz(categoryId, "보험계약의 철회기간은?", 10,
                        Arrays.asList("15일", "7일", "30일")));
                break;
        }

        return quizzes;
    }

    private QuizRequest createQuiz(Long quizTypeId, String content, Integer score, List<String> choices) {
        QuizRequest quiz = new QuizRequest();
        quiz.setQuizTypeId(quizTypeId); // `quizTypeId`에 적절한 값 전달
        quiz.setQuizContent(content);
        quiz.setQuizScore(score);
        quiz.setChoices(choices);
        return quiz;
    }
}