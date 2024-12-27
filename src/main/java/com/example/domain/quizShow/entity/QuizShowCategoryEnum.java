package com.example.domain.quizShow.entity;

public enum QuizShowCategoryEnum {
    INVESTMENT("투자"),
    SAVINGS("저축"),
    STOCK_MARKET("주식시장"),
    REAL_ESTATE("부동산"),
    CRYPTOCURRENCY("가상화폐"),
    INSURANCE("보험");

    private final String description;

    QuizShowCategoryEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}